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
    private lateinit var caloriesProgressCircle: ProgressBar
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
    private var currentSearchCall: Call<OFFV2Response>? = null

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

        val retrofit = Retrofit.Builder()
            .baseUrl("https://world.openfoodfacts.org/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        offService = retrofit.create(OFFServiceV2::class.java)
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
        val adapter = FoodAdapter(foodList) { food ->
            showQuantityDialog(food) { updatedFood ->
                // After quantity is adjusted, we mark it as selected in the adapter's logic
                // The adapter already handles updating its internal map if selectFood is called
            }
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        recyclerView.isNestedScrollingEnabled = true

        val alreadyLoggedForThisMeal = currentLoggedMeals.filter { it["mealType"] == mealType }
        val alreadyLoggedNames = alreadyLoggedForThisMeal.map { it["name"] as String }.toSet()

        // Pre-populate with existing data if needed
        alreadyLoggedForThisMeal.forEach { meal ->
            val food = Food(
                name = meal["name"] as String,
                calories = (meal["calories"] as? Long)?.toInt() ?: 0,
                protein = (meal["protein"] as? Long)?.toInt() ?: 0,
                carbs = (meal["carbs"] as? Long)?.toInt() ?: 0,
                fat = (meal["fat"] as? Long)?.toInt() ?: 0,
                quantity = (meal["quantity"] as? Double) ?: 1.0,
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
                    performSearch(query, foodList, adapter, alreadyLoggedNames, progressBar, emptyText)
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
                            performSearch(query, foodList, adapter, alreadyLoggedNames, progressBar, emptyText)
                        }
                        searchHandler.postDelayed(searchRunnable!!, 400)
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
            if (selectedFoods.isNotEmpty()) {
                syncMealsWithFirebase(selectedFoods, mealType)
                bottomSheetDialog.dismiss()
            } else {
                Toast.makeText(this, "Select at least one item", Toast.LENGTH_SHORT).show()
            }
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
                            calories = doc.getLong("calories")?.toInt() ?: 0,
                            protein = doc.getLong("protein")?.toInt() ?: 0,
                            carbs = doc.getLong("carbs")?.toInt() ?: 0,
                            fat = doc.getLong("fat")?.toInt() ?: 0,
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

    private fun performSearch(
        query: String,
        foodList: MutableList<Food>,
        adapter: FoodAdapter,
        alreadyLogged: Set<String>,
        progressBar: ProgressBar,
        emptyText: TextView
    ) {
        cancelCurrentSearch()
        progressBar.visibility = View.VISIBLE
        emptyText.visibility = View.GONE

        searchFoodFromWeb(query) { results ->
            progressBar.visibility = View.GONE
            foodList.clear()
            if (results.isNotEmpty()) {
                foodList.addAll(results)
                emptyText.visibility = View.GONE
            } else {
                emptyText.visibility = View.VISIBLE
                emptyText.text = "No results found for \"$query\""
            }
            adapter.notifyDataSetChanged()
        }
    }

    private fun searchFoodFromWeb(query: String, callback: (List<Food>) -> Unit) {
        currentSearchCall = offService.searchFood(query)
        currentSearchCall?.enqueue(object : Callback<OFFV2Response> {
            override fun onResponse(call: Call<OFFV2Response>, response: Response<OFFV2Response>) {
                if (call.isCanceled) return
                if (response.isSuccessful) {
                    val products = response.body()?.products ?: emptyList()
                    val mappedResults = products
                        .filter { !it.productName.isNullOrEmpty() }
                        .map { p ->
                            val n = p.nutriments
                            Food(
                                name = p.productName + (if (!p.brands.isNullOrEmpty()) " (${p.brands})" else ""),
                                calories = n?.calories?.toInt() ?: 0,
                                protein = n?.protein?.toInt() ?: 0,
                                carbs = n?.carbs?.toInt() ?: 0,
                                fat = n?.fat?.toInt() ?: 0,
                                unit = "100g" // Default for OFF data
                            )
                        }
                    callback(mappedResults.distinctBy { it.name })
                } else {
                    callback(emptyList())
                }
                currentSearchCall = null
            }

            override fun onFailure(call: Call<OFFV2Response>, t: Throwable) {
                if (!call.isCanceled) callback(emptyList())
                currentSearchCall = null
            }
        })
    }

    private fun syncMealsWithFirebase(selectedFoods: List<Food>, mealType: String) {
        val userId = auth.currentUser?.uid ?: return
        val dateString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate.time)
        val batch = firestore.batch()

        val selectedNames = selectedFoods.map { it.name }.toSet()
        val currentlyLoggedForThisMeal = currentLoggedMeals.filter { it["mealType"] == mealType }
        
        // Find items to add or update
        for (food in selectedFoods) {
            val existing = currentlyLoggedForThisMeal.find { it["name"] == food.name }
            if (existing == null) {
                // Add new
                val mealRef = firestore.collection("users").document(userId).collection("meals").document()
                val data = hashMapOf(
                    "name" to food.name,
                    "calories" to food.totalCalories,
                    "protein" to food.totalProtein,
                    "carbs" to food.totalCarbs,
                    "fat" to food.totalFat,
                    "quantity" to food.quantity,
                    "unit" to food.unit,
                    "mealType" to mealType,
                    "date" to dateString
                )
                batch.set(mealRef, data)
            } else {
                // Update quantity if changed
                val docId = existing["id"] as String
                val oldQty = existing["quantity"] as? Double ?: 1.0
                if (oldQty != food.quantity) {
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
        }

        // Delete items that were unselected
        for (loggedMeal in currentlyLoggedForThisMeal) {
            val name = loggedMeal["name"] as String
            val docId = loggedMeal["id"] as String
            if (!selectedNames.contains(name)) {
                val mealRef = firestore.collection("users").document(userId).collection("meals").document(docId)
                batch.delete(mealRef)
            }
        }
        
        batch.commit().addOnSuccessListener {
            Toast.makeText(this, "Diary updated", Toast.LENGTH_SHORT).show()
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
        if (dailyCalorieGoal > 0) {
            val progress = (caloriesConsumed * 100 / dailyCalorieGoal)
            caloriesProgressCircle.progress = minOf(progress, 100)
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
