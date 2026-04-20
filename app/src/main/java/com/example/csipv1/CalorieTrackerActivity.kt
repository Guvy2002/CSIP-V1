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
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

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

    // Adapters for meal sections
    private lateinit var adapterBreakfast: LoggedFoodAdapter
    private lateinit var adapterLunch: LoggedFoodAdapter
    private lateinit var adapterDinner: LoggedFoodAdapter
    private lateinit var adapterSnacks: LoggedFoodAdapter

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var usdaService: USDAService
    private lateinit var offService: OFFServiceV2
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
    }

    override fun onResume() {
        super.onResume()
        bottomNavigation.selectedItemId = R.id.navigation_diary
    }

    private fun setupRetrofit() {
        val client = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()

        val usdaRetrofit = Retrofit.Builder()
            .baseUrl("https://api.nal.usda.gov/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        usdaService = usdaRetrofit.create(USDAService::class.java)

        val offRetrofit = Retrofit.Builder()
            .baseUrl("https://world.openfoodfacts.org/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        offService = offRetrofit.create(OFFServiceV2::class.java)
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

        setupMealRecyclerViews()
    }

    private fun setupMealRecyclerViews() {
        adapterBreakfast = LoggedFoodAdapter(
            items = emptyList(),
            onEdit = { item -> showEditQuantityDialog(item) },
            onDelete = { id -> deleteMeal(id) }
        )
        findViewById<RecyclerView>(R.id.recycler_breakfast).apply {
            layoutManager = LinearLayoutManager(this@CalorieTrackerActivity)
            adapter = adapterBreakfast
        }

        adapterLunch = LoggedFoodAdapter(
            items = emptyList(),
            onEdit = { item -> showEditQuantityDialog(item) },
            onDelete = { id -> deleteMeal(id) }
        )
        findViewById<RecyclerView>(R.id.recycler_lunch).apply {
            layoutManager = LinearLayoutManager(this@CalorieTrackerActivity)
            adapter = adapterLunch
        }

        adapterDinner = LoggedFoodAdapter(
            items = emptyList(),
            onEdit = { item -> showEditQuantityDialog(item) },
            onDelete = { id -> deleteMeal(id) }
        )
        findViewById<RecyclerView>(R.id.recycler_dinner).apply {
            layoutManager = LinearLayoutManager(this@CalorieTrackerActivity)
            adapter = adapterDinner
        }

        adapterSnacks = LoggedFoodAdapter(
            items = emptyList(),
            onEdit = { item -> showEditQuantityDialog(item) },
            onDelete = { id -> deleteMeal(id) }
        )
        findViewById<RecyclerView>(R.id.recycler_snacks).apply {
            layoutManager = LinearLayoutManager(this@CalorieTrackerActivity)
            adapter = adapterSnacks
        }
    }

    private fun showEditQuantityDialog(item: Map<String, Any>) {
        val id = item["id"] as? String ?: return
        val name = item["name"] as? String ?: "Food"
        val baseCalories = (item["baseCalories"] as? Number)?.toInt() ?: 0
        val baseProtein = (item["baseProtein"] as? Number)?.toInt() ?: 0
        val baseCarbs = (item["baseCarbs"] as? Number)?.toInt() ?: 0
        val baseFat = (item["baseFat"] as? Number)?.toInt() ?: 0
        val currentQty = (item["quantity"] as? Number)?.toDouble() ?: 1.0
        val unit = item["unit"] as? String ?: "serving"

        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_adjust_quantity, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        val nameText = dialogView.findViewById<TextView>(R.id.text_dialog_food_name)
        val infoText = dialogView.findViewById<TextView>(R.id.text_dialog_food_info)
        val quantityEdit = dialogView.findViewById<EditText>(R.id.edit_quantity)
        val totalCalText = dialogView.findViewById<TextView>(R.id.text_total_calories)
        
        nameText.text = name
        infoText.text = "$baseCalories kcal per $unit"
        quantityEdit.setText(currentQty.toString())
        
        fun updateTotal() {
            val qty = quantityEdit.text.toString().toDoubleOrNull() ?: 0.0
            val total = (baseCalories * qty).toInt()
            totalCalText.text = "Total: $total kcal"
        }
        updateTotal()

        quantityEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { updateTotal() }
            override fun afterTextChanged(s: Editable?) {}
        })

        dialogView.findViewById<Button>(R.id.btn_confirm).setOnClickListener {
            val qty = quantityEdit.text.toString().toDoubleOrNull() ?: currentQty
            updateMealInFirebase(id, qty, baseCalories, baseProtein, baseCarbs, baseFat)
            dialog.dismiss()
        }
        dialogView.findViewById<Button>(R.id.btn_cancel).setOnClickListener { dialog.dismiss() }
        
        dialog.show()
    }

    private fun updateMealInFirebase(id: String, qty: Double, baseCal: Int, basePro: Int, baseCarb: Int, baseFat: Int) {
        val userId = auth.currentUser?.uid ?: return
        val updates = mapOf(
            "quantity" to qty,
            "calories" to (baseCal * qty).toInt(),
            "protein" to (basePro * qty).toInt(),
            "carbs" to (baseCarb * qty).toInt(),
            "fat" to (baseFat * qty).toInt()
        )
        firestore.collection("users").document(userId).collection("meals").document(id)
            .update(updates)
            .addOnSuccessListener { Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show() }
    }

    private fun setupListeners() {
        textDate.setOnClickListener { showDatePicker() }
        btnCalendar.setOnClickListener { showDatePicker() }

        btnAddBreakfast.setOnClickListener { showAddFoodBottomSheet("Breakfast") }
        btnAddLunch.setOnClickListener { showAddFoodBottomSheet("Lunch") }
        btnAddDinner.setOnClickListener { showAddFoodBottomSheet("Dinner") }
        btnAddSnacks.setOnClickListener { showAddFoodBottomSheet("Snacks") }
    }

    private fun showAddFoodBottomSheet(mealType: String) {
        val bottomSheetDialog = BottomSheetDialog(this)
        val view = LayoutInflater.from(this).inflate(R.layout.layout_add_food_bottom_sheet, null)
        bottomSheetDialog.setContentView(view)

        bottomSheetDialog.setOnShowListener { dialog ->
            val d = dialog as BottomSheetDialog
            val bottomSheet = d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            if (bottomSheet != null) {
                bottomSheet.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
                val behavior = BottomSheetBehavior.from(bottomSheet)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.skipCollapsed = true
            }
        }

        val titleText = view.findViewById<TextView>(R.id.text_meal_title)
        val searchEdit = view.findViewById<EditText>(R.id.edit_food_search)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_food_selection)
        val saveButton = view.findViewById<Button>(R.id.btn_save_selection)
        val progressBar = view.findViewById<ProgressBar>(R.id.progress_loading_search)
        val emptyText = view.findViewById<TextView>(R.id.text_empty_state)

        titleText.text = "Add to $mealType"

        val foodList = mutableListOf<Food>()
        
        lateinit var adapter: FoodAdapter
        adapter = FoodAdapter(foodList) { food ->
            if (adapter.isSelected(food)) {
                adapter.toggleSelection(food)
            } else {
                showQuantityDialog(food) { updatedFood ->
                    adapter.selectFood(updatedFood)
                }
            }
        }
        
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        recyclerView.isNestedScrollingEnabled = true

        val alreadyLoggedForThisMeal = currentLoggedMeals.filter { it["mealType"] == mealType }
        val alreadyLoggedNames = alreadyLoggedForThisMeal.map { it["name"] as String }.toSet()

        alreadyLoggedForThisMeal.forEach { meal ->
            val food = Food(
                name = meal["name"] as String,
                calories = (meal["baseCalories"] as? Number)?.toInt() ?: ((meal["calories"] as? Number)?.toInt() ?: 0),
                protein = (meal["baseProtein"] as? Number)?.toInt() ?: ((meal["protein"] as? Number)?.toInt() ?: 0),
                carbs = (meal["baseCarbs"] as? Number)?.toInt() ?: ((meal["carbs"] as? Number)?.toInt() ?: 0),
                fat = (meal["baseFat"] as? Number)?.toInt() ?: ((meal["fat"] as? Number)?.toInt() ?: 0),
                quantity = (meal["quantity"] as? Number)?.toDouble() ?: 1.0,
                unit = (meal["unit"] as? String) ?: "serving"
            )
            adapter.selectFood(food)
        }

        loadRecentFoods(foodList, adapter, alreadyLoggedNames, emptyText)

        searchEdit.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)
            ) {
                val query = searchEdit.text.toString().trim()
                searchHandler.removeCallbacksAndMessages(null)
                if (query.isNotEmpty()) {
                    performPrioritySearch(query, foodList, adapter, progressBar, emptyText)
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                }
                true
            } else false
        }

        searchEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchRunnable?.let { searchHandler.removeCallbacks(it) }

                val query = s.toString().trim()
                when {
                    query.length >= 2 -> {
                        searchRunnable = Runnable {
                            performPrioritySearch(query, foodList, adapter, progressBar, emptyText)
                        }
                        searchHandler.postDelayed(searchRunnable!!, 600)
                    }
                    query.isEmpty() -> {
                        cancelCurrentSearch()
                        progressBar.visibility = View.GONE
                        loadRecentFoods(foodList, adapter, alreadyLoggedNames, emptyText)
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        saveButton.setOnClickListener {
            val selectedFoods = adapter.getSelectedFoods()
            syncMealsWithFirebase(selectedFoods, mealType)
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.setOnDismissListener {
            cancelCurrentSearch()
            searchHandler.removeCallbacksAndMessages(null)
        }

        bottomSheetDialog.show()
    }

    private fun showQuantityDialog(food: Food, onConfirm: (Food) -> Unit) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_adjust_quantity, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        val nameText = dialogView.findViewById<TextView>(R.id.text_dialog_food_name)
        val infoText = dialogView.findViewById<TextView>(R.id.text_dialog_food_info)
        val quantityEdit = dialogView.findViewById<EditText>(R.id.edit_quantity)
        val unitText = dialogView.findViewById<TextView>(R.id.text_unit)
        val totalCalText = dialogView.findViewById<TextView>(R.id.text_total_calories)
        val btnCancel = dialogView.findViewById<Button>(R.id.btn_cancel)
        val btnConfirm = dialogView.findViewById<Button>(R.id.btn_confirm)

        nameText.text = food.name
        infoText.text = "${food.calories} kcal per ${food.unit}"
        unitText.text = food.unit
        quantityEdit.setText(food.quantity.toString())
        
        fun updateTotal() {
            val qty = quantityEdit.text.toString().toDoubleOrNull() ?: 0.0
            val total = (food.calories * qty).toInt()
            totalCalText.text = "Total: $total kcal"
        }

        updateTotal()

        quantityEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateTotal()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        btnCancel.setOnClickListener { dialog.dismiss() }

        btnConfirm.setOnClickListener {
            val qty = quantityEdit.text.toString().toDoubleOrNull() ?: 1.0
            food.quantity = qty
            onConfirm(food)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun cancelCurrentSearch() {
        currentSearchCall?.cancel()
        currentSearchCall = null
        currentOFFCall?.cancel()
        currentOFFCall = null
    }

    private fun loadRecentFoods(
        foodList: MutableList<Food>,
        adapter: FoodAdapter,
        alreadyLogged: Set<String>,
        emptyText: TextView
    ) {
        val userId = auth.currentUser?.uid ?: return
        firestore.collection("users").document(userId).collection("meals")
            .orderBy("date", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(25)
            .get()
            .addOnSuccessListener { docs ->
                if (!docs.isEmpty) {
                    val recents = docs.map { doc ->
                        Food(
                            name = doc.getString("name") ?: "Unknown",
                            calories = (doc.get("baseCalories") as? Number ?: doc.get("calories") as? Number ?: 0).toInt(),
                            protein = (doc.get("baseProtein") as? Number ?: doc.get("protein") as? Number ?: 0).toInt(),
                            carbs = (doc.get("baseCarbs") as? Number ?: doc.get("carbs") as? Number ?: 0).toInt(),
                            fat = (doc.get("baseFat") as? Number ?: doc.get("fat") as? Number ?: 0).toInt(),
                            unit = doc.getString("unit") ?: "serving"
                        )
                    }.distinctBy { it.name }

                    foodList.clear()
                    foodList.addAll(recents)
                    adapter.notifyDataSetChanged()
                    emptyText.visibility = View.VISIBLE
                    emptyText.text = "Recent Foods"
                } else {
                    emptyText.visibility = View.VISIBLE
                    emptyText.text = "Search for food to see results"
                }
            }
    }

    private fun performPrioritySearch(
        query: String,
        foodList: MutableList<Food>,
        adapter: FoodAdapter,
        progressBar: ProgressBar,
        emptyText: TextView
    ) {
        cancelCurrentSearch()
        progressBar.visibility = View.VISIBLE
        emptyText.visibility = View.GONE
        
        // Step 1: Search Firestore (Desi/Local foods)
        firestore.collection("foods")
            .whereGreaterThanOrEqualTo("name", query)
            .whereLessThanOrEqualTo("name", query + "\uf8ff")
            .get()
            .addOnSuccessListener { snapshot ->
                val localResults = snapshot.map { doc ->
                    Food(
                        name = doc.getString("name") ?: "Unknown",
                        calories = (doc.get("calories") as? Number)?.toInt() ?: 0,
                        protein = (doc.get("protein") as? Number)?.toInt() ?: 0,
                        carbs = (doc.get("carbs") as? Number)?.toInt() ?: 0,
                        fat = (doc.get("fat") as? Number)?.toInt() ?: 0,
                        unit = doc.getString("unit") ?: "serving"
                    )
                }

                // Step 2 & 3: Search both USDA and Open Food Facts
                searchAllAPIs(query) { apiResults ->
                    progressBar.visibility = View.GONE
                    foodList.clear()
                    
                    // Priority order: 1. Firestore, 2. Combined APIs
                    foodList.addAll(localResults)
                    foodList.addAll(apiResults)

                    if (foodList.isEmpty()) {
                        emptyText.visibility = View.VISIBLE
                        emptyText.text = "No results found for \"$query\""
                    } else {
                        emptyText.visibility = View.GONE
                    }
                    adapter.notifyDataSetChanged()
                }
            }
            .addOnFailureListener {
                searchAllAPIs(query) { apiResults ->
                    progressBar.visibility = View.GONE
                    foodList.clear()
                    foodList.addAll(apiResults)
                    if (foodList.isEmpty()) {
                        emptyText.visibility = View.VISIBLE
                        emptyText.text = "No results found"
                    }
                    adapter.notifyDataSetChanged()
                }
            }
    }

    private fun searchAllAPIs(query: String, callback: (List<Food>) -> Unit) {
        val allResults = mutableListOf<Food>()
        var pendingCalls = 2

        fun checkDone() {
            pendingCalls--
            if (pendingCalls == 0) {
                callback(allResults.distinctBy { it.name })
            }
        }

        // Search USDA
        currentSearchCall = usdaService.searchFood(query)
        currentSearchCall?.enqueue(object : Callback<USDASearchResponse> {
            override fun onResponse(call: Call<USDASearchResponse>, response: Response<USDASearchResponse>) {
                if (!call.isCanceled && response.isSuccessful) {
                    response.body()?.foods?.forEach { uf ->
                        val nuts = uf.foodNutrients
                        allResults.add(Food(
                            name = uf.description + (if (uf.brandOwner != null) " (${uf.brandOwner})" else ""),
                            calories = nuts?.find { it.name?.contains("Energy", true) == true }?.value?.toInt() ?: 0,
                            protein = nuts?.find { it.name?.contains("Protein", true) == true }?.value?.toInt() ?: 0,
                            carbs = nuts?.find { it.name?.contains("Carbohydrate", true) == true }?.value?.toInt() ?: 0,
                            fat = nuts?.find { it.name?.contains("Total lipid", true) == true }?.value?.toInt() ?: 0,
                            unit = "100g"
                        ))
                    }
                }
                checkDone()
            }
            override fun onFailure(call: Call<USDASearchResponse>, t: Throwable) { checkDone() }
        })

        // Search Open Food Facts
        currentOFFCall = offService.searchFood(query)
        currentOFFCall?.enqueue(object : Callback<OFFV2Response> {
            override fun onResponse(call: Call<OFFV2Response>, response: Response<OFFV2Response>) {
                if (!call.isCanceled && response.isSuccessful) {
                    response.body()?.products?.forEach { p ->
                        val n = p.nutriments
                        allResults.add(Food(
                            name = (p.productName ?: "Unknown") + (if (!p.brands.isNullOrEmpty()) " (${p.brands})" else ""),
                            calories = n?.calories?.toInt() ?: 0,
                            protein = n?.protein?.toInt() ?: 0,
                            carbs = n?.carbs?.toInt() ?: 0,
                            fat = n?.fat?.toInt() ?: 0,
                            unit = "100g"
                        ))
                    }
                }
                checkDone()
            }
            override fun onFailure(call: Call<OFFV2Response>, t: Throwable) { checkDone() }
        })
    }

    private fun syncMealsWithFirebase(selectedFoods: List<Food>, mealType: String) {
        val userId = auth.currentUser?.uid ?: return
        val username = auth.currentUser?.displayName ?: "Someone"
        val dateString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate.time)
        val batch = firestore.batch()

        val selectedNames = selectedFoods.map { it.name }.toSet()
        val currentlyLoggedForThisMeal = currentLoggedMeals.filter { it["mealType"] == mealType }
        
        for (food in selectedFoods) {
            val existing = currentlyLoggedForThisMeal.find { it["name"] == food.name }
            if (existing == null) {
                val mealRef = firestore.collection("users").document(userId).collection("meals").document()
                val data = hashMapOf(
                    "name" to food.name,
                    "calories" to food.totalCalories,
                    "protein" to food.totalProtein,
                    "carbs" to food.totalCarbs,
                    "fat" to food.totalFat,
                    "baseCalories" to food.calories,
                    "baseProtein" to food.protein,
                    "baseCarbs" to food.carbs,
                    "baseFat" to food.fat,
                    "quantity" to food.quantity,
                    "unit" to food.unit,
                    "mealType" to mealType,
                    "date" to dateString
                )
                batch.set(mealRef, data)

                // Live Community Update for MEAL
                val activityId = firestore.collection("activity_feed").document().id
                val activity = hashMapOf(
                    "id" to activityId,
                    "userId" to userId,
                    "username" to username,
                    "type" to "MEAL",
                    "content" to "just logged ${food.name} for $mealType! 🍲",
                    "timestamp" to System.currentTimeMillis(),
                    "highFives" to emptyList<String>()
                )
                batch.set(firestore.collection("activity_feed").document(activityId), activity)

            } else {
                val docId = existing["id"] as String
                val mealRef = firestore.collection("users").document(userId).collection("meals").document(docId)
                batch.update(mealRef, mapOf(
                    "quantity" to food.quantity,
                    "calories" to food.totalCalories,
                    "protein" to food.totalProtein,
                    "carbs" to food.totalCarbs,
                    "fat" to food.totalFat
                ))
            }
        }

        for (loggedMeal in currentlyLoggedForThisMeal) {
            val name = loggedMeal["name"] as String
            val docId = loggedMeal["id"] as String
            if (!selectedNames.contains(name)) {
                val mealRef = firestore.collection("users").document(userId).collection("meals").document(docId)
                batch.delete(mealRef)
            }
        }
        
        batch.commit()
            .addOnSuccessListener {
                Toast.makeText(this, "Diary updated", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to update: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun deleteMeal(id: String) {
        val userId = auth.currentUser?.uid ?: return
        firestore.collection("users").document(userId).collection("meals").document(id)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Item removed", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showDatePicker() {
        val datePickerDialog = DatePickerDialog(this, { _, year, month, dayOfMonth ->
            selectedDate.set(year, month, dayOfMonth)
            updateDateDisplay()
            startRealTimeMealsListener()
        }, selectedDate.get(Calendar.YEAR), selectedDate.get(Calendar.MONTH), selectedDate.get(Calendar.DAY_OF_MONTH))
        datePickerDialog.show()
    }

    private fun updateDateDisplay() {
        textDate.text = SimpleDateFormat("EEEE, MMM dd", Locale.getDefault()).format(selectedDate.time)
    }

    private fun loadUserGoals() {
        val userId = auth.currentUser?.uid ?: return
        firestore.collection("users").document(userId).collection("goals").document("current")
            .get().addOnSuccessListener { document ->
                if (document.exists()) {
                    dailyCalorieGoal = (document.get("dailyCalories") as? Number)?.toInt() ?: 2000
                    proteinTarget = (document.get("proteinTarget") as? Number)?.toInt() ?: 150
                    carbsTarget = (document.get("carbsTarget") as? Number)?.toInt() ?: 250
                    fatTarget = (document.get("fatTarget") as? Number)?.toInt() ?: 70
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
            .addSnapshotListener { snapshots, e ->
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
                    
                    // Filter and update individual meal adapters
                    adapterBreakfast.updateData(currentLoggedMeals.filter { it["mealType"] == "Breakfast" })
                    adapterLunch.updateData(currentLoggedMeals.filter { it["mealType"] == "Lunch" })
                    adapterDinner.updateData(currentLoggedMeals.filter { it["mealType"] == "Dinner" })
                    adapterSnacks.updateData(currentLoggedMeals.filter { it["mealType"] == "Snacks" })

                    updateCalorieDisplay()
                }
            }
    }

    private fun updateCalorieDisplay() {
        val remaining = dailyCalorieGoal - caloriesConsumed
        textCaloriesConsumed.text = caloriesConsumed.toString()
        textCaloriesGoal.text = "Goal: $dailyCalorieGoal | Left: $remaining"
        textRemainingLabel.text = "Consumed"
        
        if (dailyCalorieGoal > 0) {
            val progress = (caloriesConsumed * 100 / dailyCalorieGoal)
            caloriesProgressCircle.setProgress(minOf(progress, 100), true)
        }
        
        progressProtein.progress = if (proteinTarget > 0) minOf(totalProtein * 100 / proteinTarget, 100) else 0
        progressCarbs.progress = if (carbsTarget > 0) minOf(totalCarbs * 100 / carbsTarget, 100) else 0
        progressFat.progress = if (fatTarget > 0) minOf(totalFat * 100 / fatTarget, 100) else 0
        
        labelProtein.text = "Protein: ${totalProtein}/${proteinTarget}g"
        labelCarbs.text = "Carbs: ${totalCarbs}/${carbsTarget}g"
        labelFat.text = "Fat: ${totalFat}/${fatTarget}g"
    }

    private fun setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener { item ->
            if (item.itemId == R.id.navigation_diary) return@setOnItemSelectedListener true

            val target = when (item.itemId) {
                R.id.navigation_home -> HomeActivity::class.java
                R.id.navigation_community -> CommunityActivity::class.java
                R.id.navigation_exercise -> WorkoutActivity::class.java
                else -> null
            }

            target?.let {
                navigateTo(it)
                true
            } ?: false
        }
        bottomNavigation.selectedItemId = R.id.navigation_diary
    }

    private fun navigateTo(activityClass: Class<*>) {
        if (this::class.java == activityClass) return
        val intent = Intent(this, activityClass)
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        startActivity(intent)
        overridePendingTransition(0, 0)
    }

    override fun onDestroy() {
        super.onDestroy()
        mealsListener?.remove()
        searchHandler.removeCallbacksAndMessages(null)
        cancelCurrentSearch()
    }
}
