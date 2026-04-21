package com.example.csipv1

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

class CalorieTrackerActivity : BaseActivity() {

    private lateinit var textDate: TextView
    private lateinit var btnCalendar: ImageButton
    private lateinit var caloriesProgressCircle: CircularProgressIndicator
    private lateinit var textCaloriesConsumed: TextView
    private lateinit var textCaloriesGoal: TextView
    private lateinit var textRemainingLabel: TextView
    private lateinit var bottomNavigation: BottomNavigationView

    private lateinit var progressProtein: ProgressBar
    private lateinit var progressCarbs: ProgressBar
    private lateinit var progressFat: ProgressBar
    private lateinit var labelProtein: TextView
    private lateinit var labelCarbs: TextView
    private lateinit var labelFat: TextView

    private lateinit var btnAddBreakfast: ImageButton
    private lateinit var btnAddLunch: ImageButton
    private lateinit var btnAddDinner: ImageButton
    private lateinit var btnAddSnacks: ImageButton
    private lateinit var btnShortcutCustomDish: Button

    // Adapters for meal sections
    private lateinit var adapterBreakfast: LoggedFoodAdapter
    private lateinit var adapterLunch: LoggedFoodAdapter
    private lateinit var adapterDinner: LoggedFoodAdapter
    private lateinit var adapterSnacks: LoggedFoodAdapter

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var usdaService: USDAService
    private lateinit var offService: OFFServiceV2
    private lateinit var edamamService: EdamamService
    private lateinit var fatSecretService: FatSecretService
    private var mealsListener: ListenerRegistration? = null

    private var dailyCalorieGoal: Int = 2000
    private var proteinTarget: Int = 150
    private var carbsTarget: Int = 250
    private var fatTarget: Int = 70

    private var caloriesConsumed: Int = 0
    private var totalProtein: Int = 0
    private var totalCarbs: Int = 0
    private var totalFat: Int = 0
    private var selectedDate: Calendar = Calendar.getInstance()
    private var currentLoggedMeals = mutableListOf<Map<String, Any>>()

    private val searchHandler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null
    private var currentSearchCall: Call<USDASearchResponse>? = null
    private var currentOFFCall: Call<OFFV2Response>? = null
    private var currentEdamamCall: Call<EdamamResponse>? = null
    private var currentFSCall: Call<FatSecretSearchResponse>? = null

    // FatSecret Token Management
    private var fsAccessToken: String? = null
    private var fsTokenExpiry: Long = 0

    // Cache for local foods from JSON assets
    @Volatile
    private var allLocalFoods: List<Food>? = null
    private var isPreloadingLocal = false

    // API Keys
    private val USDA_API_KEY = "a1ahWqSqKN5iNvPVm8IbeGtdfdC1FpwCNKQhWdWi"
    private val EDAMAM_APP_ID = "0088825d"
    private val EDAMAM_APP_KEY = "f565b9911e86a0b9a803567d266df16e"
    private val FS_CLIENT_ID = "7e89ac095d9f48e3a9d466266b62a39e"
    private val FS_CLIENT_SECRET = "573939659eaa4d90bda129ab6d0c37fc"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calorie_tracker)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        setupRetrofit()
        initializeViews()
        setupListeners()
        updateDateDisplay()
        loadUserGoals()
        startRealTimeMealsListener()
        setupBottomNavigation()
        preloadLocalFoods()
    }

    private fun preloadLocalFoods() {
        if (allLocalFoods != null || isPreloadingLocal) return
        isPreloadingLocal = true
        thread {
            try {
                val files = listOf(
                    "drinks.json", "drinks2.json", "fast_foods.json", "fast_foods2.json",
                    "global_foods.json", "global_foods2.json", "supermarket_products.json",
                    "supermarket_products2.json", "traditional_foods.json", "traditional_foods2.json"
                )
                val list = mutableListOf<Food>()
                val gson = Gson()
                val type = object : TypeToken<List<Map<String, Any>>>() {}.type
                files.forEach { fileName ->
                    try {
                        val jsonString = assets.open(fileName).bufferedReader().use { it.readText() }
                        val foods: List<Map<String, Any>> = gson.fromJson(jsonString, type)
                        foods.forEach { map ->
                            list.add(Food(
                                name = map["name"] as? String ?: "",
                                calories = (map["calories"] as? Number)?.toInt() ?: 0,
                                protein = (map["protein"] as? Number)?.toInt() ?: 0,
                                carbs = (map["carbs"] as? Number)?.toInt() ?: 0,
                                fat = (map["fat"] as? Number)?.toInt() ?: 0,
                                unit = map["unit"] as? String ?: "serving"
                            ))
                        }
                    } catch (e: Exception) { Log.e("LocalSearch", "Error loading $fileName", e) }
                }
                allLocalFoods = list
            } finally { isPreloadingLocal = false }
        }
    }

    private fun setupRetrofit() {
        val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()

        usdaService = Retrofit.Builder().baseUrl("https://api.nal.usda.gov/").client(client)
            .addConverterFactory(GsonConverterFactory.create()).build().create(USDAService::class.java)

        offService = Retrofit.Builder().baseUrl("https://world.openfoodfacts.org/").client(client)
            .addConverterFactory(GsonConverterFactory.create()).build().create(OFFServiceV2::class.java)

        edamamService = Retrofit.Builder().baseUrl("https://api.edamam.com/").client(client)
            .addConverterFactory(GsonConverterFactory.create()).build().create(EdamamService::class.java)

        fatSecretService = Retrofit.Builder().baseUrl("https://oauth.fatsecret.com/").client(client)
            .addConverterFactory(GsonConverterFactory.create()).build().create(FatSecretService::class.java)
    }

    private fun getFatSecretToken(callback: (String?) -> Unit) {
        if (fsAccessToken != null && System.currentTimeMillis() < fsTokenExpiry) {
            callback(fsAccessToken)
            return
        }
        val authHeader = "Basic " + Base64.encodeToString("$FS_CLIENT_ID:$FS_CLIENT_SECRET".toByteArray(), Base64.NO_WRAP)
        fatSecretService.getAccessToken(authHeader).enqueue(object : Callback<FatSecretTokenResponse> {
            override fun onResponse(call: Call<FatSecretTokenResponse>, response: Response<FatSecretTokenResponse>) {
                if (response.isSuccessful) {
                    fsAccessToken = response.body()?.access_token
                    fsTokenExpiry = System.currentTimeMillis() + ((response.body()?.expires_in ?: 3600) * 1000)
                    callback(fsAccessToken)
                } else {
                    Log.e("FatSecret", "Token Error: ${response.code()}")
                    callback(null)
                }
            }
            override fun onFailure(call: Call<FatSecretTokenResponse>, t: Throwable) { callback(null) }
        })
    }

    private fun initializeViews() {
        textDate = findViewById(R.id.text_date)
        btnCalendar = findViewById(R.id.btn_calendar)
        caloriesProgressCircle = findViewById(R.id.calories_progress_circle)
        textCaloriesConsumed = findViewById(R.id.text_calories_consumed)
        textCaloriesGoal = findViewById(R.id.text_calories_goal)
        textRemainingLabel = findViewById(R.id.text_remaining_label)
        bottomNavigation = findViewById(R.id.bottom_navigation)
        progressProtein = findViewById(R.id.progress_protein)
        progressCarbs = findViewById(R.id.progress_carbs)
        progressFat = findViewById(R.id.progress_fat)
        labelProtein = findViewById(R.id.label_protein)
        labelCarbs = findViewById(R.id.label_carbs)
        labelFat = findViewById(R.id.label_fat)
        btnAddBreakfast = findViewById(R.id.btn_add_breakfast)
        btnAddLunch = findViewById(R.id.btn_add_lunch)
        btnAddDinner = findViewById(R.id.btn_add_dinner)
        btnAddSnacks = findViewById(R.id.btn_add_snacks)
        btnShortcutCustomDish = findViewById(R.id.btn_shortcut_custom_dish)
        setupMealRecyclerViews()
    }

    private fun setupMealRecyclerViews() {
        adapterBreakfast = LoggedFoodAdapter(emptyList(), { showEditQuantityDialog(it) }, { deleteMeal(it) })
        findViewById<RecyclerView>(R.id.recycler_breakfast).apply { layoutManager = LinearLayoutManager(this@CalorieTrackerActivity); adapter = adapterBreakfast }
        adapterLunch = LoggedFoodAdapter(emptyList(), { showEditQuantityDialog(it) }, { deleteMeal(it) })
        findViewById<RecyclerView>(R.id.recycler_lunch).apply { layoutManager = LinearLayoutManager(this@CalorieTrackerActivity); adapter = adapterLunch }
        adapterDinner = LoggedFoodAdapter(emptyList(), { showEditQuantityDialog(it) }, { deleteMeal(it) })
        findViewById<RecyclerView>(R.id.recycler_dinner).apply { layoutManager = LinearLayoutManager(this@CalorieTrackerActivity); adapter = adapterDinner }
        adapterSnacks = LoggedFoodAdapter(emptyList(), { showEditQuantityDialog(it) }, { deleteMeal(it) })
        findViewById<RecyclerView>(R.id.recycler_snacks).apply { layoutManager = LinearLayoutManager(this@CalorieTrackerActivity); adapter = adapterSnacks }
    }

    private fun showEditQuantityDialog(item: Map<String, Any>) {
        val id = item["id"] as? String ?: return
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_adjust_quantity, null)
        val dialog = AlertDialog.Builder(this).setView(dialogView).create()
        dialogView.findViewById<TextView>(R.id.text_dialog_food_name).text = item["name"] as? String ?: "Food"
        val qtyEdit = dialogView.findViewById<EditText>(R.id.edit_quantity)
        qtyEdit.setText((item["quantity"] as? Number)?.toString() ?: "1.0")
        dialogView.findViewById<Button>(R.id.btn_confirm).setOnClickListener {
            val qty = qtyEdit.text.toString().toDoubleOrNull() ?: 1.0
            updateMealInFirebase(id, qty, (item["baseCalories"] as? Number)?.toInt() ?: 0, (item["baseProtein"] as? Number)?.toInt() ?: 0, (item["baseCarbs"] as? Number)?.toInt() ?: 0, (item["baseFat"] as? Number)?.toInt() ?: 0)
            dialog.dismiss()
        }
        dialogView.findViewById<Button>(R.id.btn_cancel).setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun updateMealInFirebase(id: String, qty: Double, bCal: Int, bPro: Int, bCarb: Int, bFat: Int) {
        val userId = auth.currentUser?.uid ?: return
        val updates = mapOf("quantity" to qty, "calories" to (bCal * qty).toInt(), "protein" to (bPro * qty).toInt(), "carbs" to (bCarb * qty).toInt(), "fat" to (bFat * qty).toInt())
        firestore.collection("users").document(userId).collection("meals").document(id).update(updates)
    }

    private fun setupListeners() {
        textDate.setOnClickListener { showDatePicker() }
        btnCalendar.setOnClickListener { showDatePicker() }
        btnAddBreakfast.setOnClickListener { showAddFoodBottomSheet("Breakfast") }
        btnAddLunch.setOnClickListener { showAddFoodBottomSheet("Lunch") }
        btnAddDinner.setOnClickListener { showAddFoodBottomSheet("Dinner") }
        btnAddSnacks.setOnClickListener { showAddFoodBottomSheet("Snacks") }
        btnShortcutCustomDish.setOnClickListener { startActivity(Intent(this, AddCustomDishActivity::class.java)) }
    }

    private fun showAddFoodBottomSheet(mealType: String) {
        val bottomSheetDialog = BottomSheetDialog(this)
        val view = LayoutInflater.from(this).inflate(R.layout.layout_add_food_bottom_sheet, null)
        bottomSheetDialog.setContentView(view)
        val searchEdit = view.findViewById<EditText>(R.id.edit_food_search)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_food_selection)
        val progressBar = view.findViewById<ProgressBar>(R.id.progress_loading_search)
        val emptyText = view.findViewById<TextView>(R.id.text_empty_state)
        val foodList = mutableListOf<Food>()
        
        lateinit var adapter: FoodAdapter
        adapter = FoodAdapter(foodList) { food ->
            if (adapter.isSelected(food)) adapter.toggleSelection(food)
            else showQuantityDialog(food) { adapter.selectFood(it) }
        }
        
        recyclerView.layoutManager = LinearLayoutManager(this); recyclerView.adapter = adapter
        searchEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val q = s.toString().trim()
                if (q.length >= 2) {
                    searchRunnable?.let { searchHandler.removeCallbacks(it) }
                    searchRunnable = Runnable { performPrioritySearch(q, foodList, adapter, progressBar, emptyText) }
                    searchHandler.postDelayed(searchRunnable!!, 300)
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })
        view.findViewById<Button>(R.id.btn_save_selection).setOnClickListener {
            syncMealsWithFirebase(adapter.getSelectedFoods(), mealType)
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.show()
    }

    private fun showQuantityDialog(food: Food, onConfirm: (Food) -> Unit) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_adjust_quantity, null)
        val dialog = AlertDialog.Builder(this).setView(dialogView).create()
        dialogView.findViewById<TextView>(R.id.text_dialog_food_name).text = food.name
        val qtyEdit = dialogView.findViewById<EditText>(R.id.edit_quantity)
        qtyEdit.setText(food.quantity.toString())
        dialogView.findViewById<Button>(R.id.btn_confirm).setOnClickListener {
            food.quantity = qtyEdit.text.toString().toDoubleOrNull() ?: 1.0
            onConfirm(food)
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun cancelCurrentSearch() {
        currentSearchCall?.cancel(); currentOFFCall?.cancel(); currentEdamamCall?.cancel(); currentFSCall?.cancel()
    }

    private fun performPrioritySearch(query: String, foodList: MutableList<Food>, adapter: FoodAdapter, bar: ProgressBar, empty: TextView) {
        cancelCurrentSearch()
        bar.visibility = View.VISIBLE
        empty.visibility = View.GONE
        
        // Instant local results
        foodList.clear()
        foodList.addAll(allLocalFoods?.filter { it.name.contains(query, true) }?.take(50) ?: emptyList())
        adapter.notifyDataSetChanged()
        
        searchAllAPIs(query, { apiResults ->
            val unique = apiResults.filter { apiF -> foodList.none { it.name.equals(apiF.name, true) } }
            foodList.addAll(unique)
            adapter.notifyDataSetChanged()
        }, {
            // On Complete
            bar.visibility = View.GONE
            if (foodList.isEmpty()) {
                empty.visibility = View.VISIBLE
                empty.text = "No results found for \"$query\""
            }
        })
    }

    private var pendingAPICount = 0

    private fun searchAllAPIs(query: String, onPartialResults: (List<Food>) -> Unit, onComplete: () -> Unit) {
        pendingAPICount = 4
        
        fun checkDone() {
            pendingAPICount--
            if (pendingAPICount <= 0) onComplete()
        }

        // USDA
        usdaService.searchFood(query, USDA_API_KEY).enqueue(object : Callback<USDASearchResponse> {
            override fun onResponse(call: Call<USDASearchResponse>, response: Response<USDASearchResponse>) {
                if (response.isSuccessful) {
                    val res = response.body()?.foods?.map { uf ->
                        val n = uf.foodNutrients
                        Food(uf.description ?: "Unknown", n?.find { it.name?.contains("Energy", true) == true }?.value?.toInt() ?: 0, n?.find { it.name?.contains("Protein", true) == true }?.value?.toInt() ?: 0, n?.find { it.name?.contains("Carbohydrate", true) == true }?.value?.toInt() ?: 0, n?.find { it.name?.contains("Total lipid", true) == true }?.value?.toInt() ?: 0, unit = "100g")
                    } ?: emptyList()
                    onPartialResults(res)
                }
                checkDone()
            }
            override fun onFailure(call: Call<USDASearchResponse>, t: Throwable) { checkDone() }
        })

        // OFF
        offService.searchFood(query).enqueue(object : Callback<OFFV2Response> {
            override fun onResponse(call: Call<OFFV2Response>, response: Response<OFFV2Response>) {
                if (response.isSuccessful) {
                    val res = response.body()?.products?.map { p ->
                        val n = p.nutriments
                        Food(p.productName ?: "Unknown", n?.calories?.toInt() ?: 0, n?.protein?.toInt() ?: 0, n?.carbs?.toInt() ?: 0, n?.fat?.toInt() ?: 0, unit = "100g")
                    } ?: emptyList()
                    onPartialResults(res)
                }
                checkDone()
            }
            override fun onFailure(call: Call<OFFV2Response>, t: Throwable) { checkDone() }
        })

        // Edamam
        edamamService.searchFood(query, EDAMAM_APP_ID, EDAMAM_APP_KEY).enqueue(object : Callback<EdamamResponse> {
            override fun onResponse(call: Call<EdamamResponse>, response: Response<EdamamResponse>) {
                if (response.isSuccessful) {
                    val res = response.body()?.hints?.map { h ->
                        val f = h.food
                        Food(f.label, f.nutrients.calories.toInt(), f.nutrients.protein.toInt(), f.nutrients.carbs.toInt(), f.nutrients.fat.toInt(), unit = "100g")
                    } ?: emptyList()
                    onPartialResults(res)
                }
                checkDone()
            }
            override fun onFailure(call: Call<EdamamResponse>, t: Throwable) { checkDone() }
        })

        // FatSecret
        getFatSecretToken { token ->
            if (token != null) {
                fatSecretService.searchFood("Bearer $token", query = query).enqueue(object : Callback<FatSecretSearchResponse> {
                    override fun onResponse(call: Call<FatSecretSearchResponse>, response: Response<FatSecretSearchResponse>) {
                        if (response.isSuccessful) {
                            val res = response.body()?.foods?.food?.map { f ->
                                Food(f.food_name + " (FatSecret)", 0, 0, 0, 0, unit = "serving")
                            } ?: emptyList()
                            onPartialResults(res)
                        }
                        checkDone()
                    }
                    override fun onFailure(call: Call<FatSecretSearchResponse>, t: Throwable) { checkDone() }
                })
            } else { checkDone() }
        }
    }

    private fun syncMealsWithFirebase(selectedFoods: List<Food>, mealType: String) {
        val userId = auth.currentUser?.uid ?: return
        val dateString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate.time)
        val batch = firestore.batch()
        for (food in selectedFoods) {
            val mealRef = firestore.collection("users").document(userId).collection("meals").document()
            batch.set(mealRef, hashMapOf("name" to food.name, "calories" to food.totalCalories, "protein" to food.totalProtein, "carbs" to food.totalCarbs, "fat" to food.totalFat, "baseCalories" to food.calories, "quantity" to food.quantity, "mealType" to mealType, "date" to dateString))
        }
        batch.commit().addOnSuccessListener { Toast.makeText(this, "Diary updated", Toast.LENGTH_SHORT).show() }
    }

    private fun deleteMeal(id: String) {
        val userId = auth.currentUser?.uid ?: return
        firestore.collection("users").document(userId).collection("meals").document(id).delete()
    }

    private fun showDatePicker() {
        DatePickerDialog(this, { _, year, month, day ->
            selectedDate.set(year, month, day)
            updateDateDisplay(); startRealTimeMealsListener()
        }, selectedDate.get(Calendar.YEAR), selectedDate.get(Calendar.MONTH), selectedDate.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun updateDateDisplay() {
        textDate.text = SimpleDateFormat("EEEE, MMM dd", Locale.getDefault()).format(selectedDate.time)
    }

    private fun loadUserGoals() {
        val userId = auth.currentUser?.uid ?: return
        firestore.collection("users").document(userId).collection("goals").document("current").get().addOnSuccessListener { doc ->
            if (doc.exists()) {
                dailyCalorieGoal = (doc.get("dailyCalories") as? Number)?.toInt() ?: 2000
                updateCalorieDisplay()
            }
        }
    }

    private fun startRealTimeMealsListener() {
        val userId = auth.currentUser?.uid ?: return
        val dateString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate.time)
        mealsListener?.remove()
        mealsListener = firestore.collection("users").document(userId).collection("meals")
            .whereEqualTo("date", dateString)
            .addSnapshotListener { snapshots, _ ->
                if (snapshots != null) {
                    currentLoggedMeals.clear()
                    var c = 0; var p = 0; var cb = 0; var f = 0
                    for (doc in snapshots) {
                        currentLoggedMeals.add(doc.data + ("id" to doc.id))
                        c += (doc.get("calories") as? Number)?.toInt() ?: 0
                        p += (doc.get("protein") as? Number)?.toInt() ?: 0
                        cb += (doc.get("carbs") as? Number)?.toInt() ?: 0
                        f += (doc.get("fat") as? Number)?.toInt() ?: 0
                    }
                    caloriesConsumed = c; totalProtein = p; totalCarbs = cb; totalFat = f
                    adapterBreakfast.updateData(currentLoggedMeals.filter { it["mealType"] == "Breakfast" })
                    adapterLunch.updateData(currentLoggedMeals.filter { it["mealType"] == "Lunch" })
                    adapterDinner.updateData(currentLoggedMeals.filter { it["mealType"] == "Dinner" })
                    adapterSnacks.updateData(currentLoggedMeals.filter { it["mealType"] == "Snacks" })
                    updateCalorieDisplay()
                }
            }
    }

    private fun updateCalorieDisplay() {
        textCaloriesConsumed.text = caloriesConsumed.toString()
        textCaloriesGoal.text = "Goal: $dailyCalorieGoal"
        if (dailyCalorieGoal > 0) caloriesProgressCircle.setProgress((caloriesConsumed * 100 / dailyCalorieGoal).coerceAtMost(100), true)
        labelProtein.text = "P: $totalProtein"; labelCarbs.text = "C: $totalCarbs"; labelFat.text = "F: $totalFat"
    }

    private fun setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener { item ->
            val target = when (item.itemId) {
                R.id.navigation_home -> HomeActivity::class.java
                R.id.navigation_exercise -> WorkoutActivity::class.java
                else -> null
            }
            target?.let { startActivity(Intent(this, it)); true } ?: false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mealsListener?.remove(); cancelCurrentSearch()
    }
}
