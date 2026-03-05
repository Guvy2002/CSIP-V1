package com.example.csipv1

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
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

/**
 * Optimized Calorie Tracker Activity with Instant-Response navigation and real-time data sync.
 */
class CalorieTrackerActivity : BaseActivity() {

    private lateinit var textDate: TextView
    private lateinit var btnCalendar: ImageButton
    private lateinit var caloriesProgressCircle: CircularProgressIndicator
    private lateinit var textCaloriesConsumed: TextView
    private lateinit var textCaloriesGoal: TextView
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

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var openFoodFactsService: OpenFoodFactsService
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
    private var currentSearchCall: Call<OFFSearchResponse>? = null

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
        // Ensure the correct icon is highlighted when returning to this page
        bottomNavigation.selectedItemId = R.id.navigation_diary
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    private fun setupRetrofit() {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://uk.openfoodfacts.org/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        
        openFoodFactsService = retrofit.create(OpenFoodFactsService::class.java)
    }

    private fun initializeViews() {
        textDate = findViewById(R.id.text_date)
        btnCalendar = findViewById(R.id.btn_calendar)
        caloriesProgressCircle = findViewById(R.id.calories_progress_circle)
        textCaloriesConsumed = findViewById(R.id.text_calories_consumed)
        textCaloriesGoal = findViewById(R.id.text_calories_goal)
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

        val titleText = view.findViewById<TextView>(R.id.text_meal_title)
        val searchEdit = view.findViewById<EditText>(R.id.edit_food_search)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_food_selection)
        val saveButton = view.findViewById<Button>(R.id.btn_save_selection)

        titleText.text = "Add to $mealType"
        
        val foodList = mutableListOf<Food>()
        val adapter = FoodAdapter(foodList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        val alreadyLoggedNames = currentLoggedMeals
            .filter { it["mealType"] == mealType }
            .map { it["name"] as String }
            .toSet()

        searchEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchRunnable?.let { searchHandler.removeCallbacks(it) }
                currentSearchCall?.cancel()
                
                val query = s.toString().trim()
                if (query.length > 2) {
                    searchRunnable = Runnable {
                        searchFoodFromWeb(query) { results ->
                            foodList.clear()
                            foodList.addAll(results)
                            results.forEachIndexed { index, food ->
                                if (alreadyLoggedNames.contains(food.name)) {
                                    adapter.preSelect(index)
                                }
                            }
                            adapter.notifyDataSetChanged()
                        }
                    }
                    searchHandler.postDelayed(searchRunnable!!, 300)
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        saveButton.setOnClickListener {
            val selectedFoods = adapter.getSelectedFoods()
            syncMealsWithFirebase(selectedFoods, mealType)
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.show()
    }

    private fun searchFoodFromWeb(query: String, callback: (List<Food>) -> Unit) {
        val userAgent = "MyDesiDietPro - Android - dev@example.com"
        
        currentSearchCall = openFoodFactsService.searchFood(query, userAgent)
        currentSearchCall?.enqueue(object : Callback<OFFSearchResponse> {
            override fun onResponse(call: Call<OFFSearchResponse>, response: Response<OFFSearchResponse>) {
                if (response.isSuccessful) {
                    val products = response.body()?.products ?: emptyList()
                    val mappedResults = products.filter { !it.productName.isNullOrEmpty() }.map { p ->
                        val n = p.nutriments
                        Food(
                            name = p.productName ?: "Unknown",
                            calories = parseNutrient(n?.get("energy-kcal_100g")),
                            protein = parseNutrient(n?.get("proteins_100g")),
                            carbs = parseNutrient(n?.get("carbohydrates_100g")),
                            fat = parseNutrient(n?.get("fat_100g"))
                        )
                    }
                    callback(mappedResults)
                }
            }
            override fun onFailure(call: Call<OFFSearchResponse>, t: Throwable) {
                if (!call.isCanceled) {
                    Log.e("OFF_SEARCH", "Request failed: ${t.message}")
                }
            }
        })
    }

    private fun parseNutrient(value: Any?): Int {
        return when (value) {
            is Double -> value.toInt()
            is Float -> value.toInt()
            is Int -> value
            is String -> value.toDoubleOrNull()?.toInt() ?: 0
            else -> 0
        }
    }

    private fun syncMealsWithFirebase(selectedFoods: List<Food>, mealType: String) {
        val userId = auth.currentUser?.uid ?: return
        val dateString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate.time)
        val batch = firestore.batch()

        val selectedNames = selectedFoods.map { it.name }.toSet()
        val currentlyLoggedForThisMeal = currentLoggedMeals.filter { it["mealType"] == mealType }
        val currentlyLoggedNames = currentlyLoggedForThisMeal.map { it["name"] as String }.toSet()

        for (food in selectedFoods) {
            if (!currentlyLoggedNames.contains(food.name)) {
                val mealRef = firestore.collection("users").document(userId).collection("meals").document()
                val data = hashMapOf(
                    "name" to food.name,
                    "calories" to food.calories,
                    "protein" to food.protein,
                    "carbs" to food.carbs,
                    "fat" to food.fat,
                    "mealType" to mealType,
                    "date" to dateString
                )
                batch.set(mealRef, data)
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
                    dailyCalorieGoal = document.getLong("dailyCalories")?.toInt() ?: 2000
                    proteinTarget = document.getLong("proteinTarget")?.toInt() ?: 150
                    carbsTarget = document.getLong("carbsTarget")?.toInt() ?: 250
                    fatTarget = document.getLong("fatTarget")?.toInt() ?: 70
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
                        c += (doc.getLong("calories") ?: 0).toInt()
                        p += (doc.getLong("protein") ?: 0).toInt()
                        cb += (doc.getLong("carbs") ?: 0).toInt()
                        f += (doc.getLong("fat") ?: 0).toInt()
                    }
                    caloriesConsumed = c; totalProtein = p; totalCarbs = cb; totalFat = f
                    updateCalorieDisplay()
                }
            }
    }

    private fun updateCalorieDisplay() {
        textCaloriesConsumed.text = caloriesConsumed.toString()
        textCaloriesGoal.text = "of $dailyCalorieGoal kcal"
        val progress = if (dailyCalorieGoal > 0) (caloriesConsumed * 100 / dailyCalorieGoal) else 0
        caloriesProgressCircle.progress = minOf(progress, 100)
        val indicatorColor = when {
            progress < 80 -> Color.GREEN
            progress in 80..100 -> Color.YELLOW
            else -> Color.RED
        }
        caloriesProgressCircle.setIndicatorColor(indicatorColor)
        
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
        currentSearchCall?.cancel()
    }
}
