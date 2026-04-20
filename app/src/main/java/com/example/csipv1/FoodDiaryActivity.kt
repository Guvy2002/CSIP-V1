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
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
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

class FoodDiaryActivity : AppCompatActivity() {

    private lateinit var dateTextView: TextView
    private lateinit var caloriesConsumedTextView: TextView
    private lateinit var caloriesGoalTextView: TextView
    private lateinit var caloriesProgressCircle: CircularProgressIndicator
    private lateinit var bottomNavigation: BottomNavigationView

    private lateinit var breakfastCard: CardView
    private lateinit var lunchCard: CardView
    private lateinit var dinnerCard: CardView
    private lateinit var snacksCard: CardView

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var offService: OFFServiceV2
    private var mealsListener: ListenerRegistration? = null

    private var dailyCalorieGoal = 2000
    private var caloriesConsumed = 0
    private var selectedDate: Calendar = Calendar.getInstance()
    private var currentLoggedMeals = mutableListOf<Map<String, Any>>()

    private val searchHandler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null
    private var currentSearchCall: Call<OFFV2Response>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calorietracker)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        setupRetrofit()
        initializeViews()
        setupListeners()
        updateDateDisplay()
        loadUserGoals()
        startRealTimeMealsListener()
    }

    private fun setupRetrofit() {
        val client = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://world.openfoodfacts.org/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        offService = retrofit.create(OFFServiceV2::class.java)
    }

    private fun initializeViews() {
        dateTextView = findViewById(R.id.text_date)
        caloriesConsumedTextView = findViewById(R.id.text_calories_consumed)
        caloriesGoalTextView = findViewById(R.id.text_calories_goal)
        caloriesProgressCircle = findViewById(R.id.calories_progress_circle)
        bottomNavigation = findViewById(R.id.bottom_navigation)

        breakfastCard = findViewById(R.id.card_breakfast)
        lunchCard = findViewById(R.id.card_lunch)
        dinnerCard = findViewById(R.id.card_dinner)
        snacksCard = findViewById(R.id.card_snacks)
    }

    private fun setupListeners() {
        dateTextView.setOnClickListener { showDatePicker() }

        breakfastCard.setOnClickListener { showAddFoodBottomSheet("Breakfast") }
        lunchCard.setOnClickListener { showAddFoodBottomSheet("Lunch") }
        dinnerCard.setOnClickListener { showAddFoodBottomSheet("Dinner") }
        snacksCard.setOnClickListener { showAddFoodBottomSheet("Snacks") }

        bottomNavigation.selectedItemId = R.id.navigation_diary
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    true
                }
                R.id.navigation_exercise -> {
                    startActivity(Intent(this, WorkoutActivity::class.java))
                    true
                }
                else -> true
            }
        }
    }

    private fun showAddFoodBottomSheet(mealType: String) {
        val bottomSheetDialog = BottomSheetDialog(this)
        val view = LayoutInflater.from(this).inflate(R.layout.layout_add_food_bottom_sheet, null)
        bottomSheetDialog.setContentView(view)

        val titleText = view.findViewById<TextView>(R.id.text_meal_title)
        val searchEdit = view.findViewById<EditText>(R.id.edit_food_search)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_food_selection)
        val saveButton = view.findViewById<Button>(R.id.btn_save_selection)
        val progressBar = view.findViewById<ProgressBar>(R.id.progress_loading_search)

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

        searchEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                if (query.length >= 2) {
                    searchRunnable?.let { searchHandler.removeCallbacks(it) }
                    searchRunnable = Runnable { performSearch(query, foodList, adapter, progressBar) }
                    searchHandler.postDelayed(searchRunnable!!, 500)
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        saveButton.setOnClickListener {
            val selected = adapter.getSelectedFoods()
            if (selected.isNotEmpty()) {
                syncMealsWithFirebase(selected, mealType)
                bottomSheetDialog.dismiss()
            }
        }

        bottomSheetDialog.show()
    }

    private fun showQuantityDialog(food: Food, onConfirm: (Food) -> Unit) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_adjust_quantity, null)
        val dialog = AlertDialog.Builder(this).setView(dialogView).create()

        val quantityEdit = dialogView.findViewById<EditText>(R.id.edit_quantity)
        dialogView.findViewById<TextView>(R.id.text_dialog_food_name).text = food.name
        
        dialogView.findViewById<Button>(R.id.btn_confirm).setOnClickListener {
            val qty = quantityEdit.text.toString().toDoubleOrNull() ?: 1.0
            food.quantity = qty
            onConfirm(food)
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun performSearch(query: String, list: MutableList<Food>, adapter: FoodAdapter, bar: ProgressBar) {
        bar.visibility = View.VISIBLE
        offService.searchFood(query).enqueue(object : Callback<OFFV2Response> {
            override fun onResponse(call: Call<OFFV2Response>, response: Response<OFFV2Response>) {
                bar.visibility = View.GONE
                if (response.isSuccessful) {
                    val results = response.body()?.products?.map { p ->
                        Food(p.productName ?: "Unknown", p.nutriments?.calories?.toInt() ?: 0, 
                             p.nutriments?.protein?.toInt() ?: 0, p.nutriments?.carbs?.toInt() ?: 0, 
                             p.nutriments?.fat?.toInt() ?: 0)
                    } ?: emptyList()
                    list.clear()
                    list.addAll(results)
                    adapter.notifyDataSetChanged()
                }
            }
            override fun onFailure(call: Call<OFFV2Response>, t: Throwable) { bar.visibility = View.GONE }
        })
    }

    private fun syncMealsWithFirebase(selectedFoods: List<Food>, mealType: String) {
        val userId = auth.currentUser?.uid ?: return
        val dateString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate.time)
        val batch = firestore.batch()

        for (food in selectedFoods) {
            val mealRef = firestore.collection("users").document(userId).collection("meals").document()
            batch.set(mealRef, hashMapOf(
                "name" to food.name, "calories" to food.totalCalories, "protein" to food.totalProtein,
                "carbs" to food.totalCarbs, "fat" to food.totalFat, "mealType" to mealType, "date" to dateString,
                "quantity" to food.quantity, "baseCalories" to food.calories
            ))
        }

        batch.commit().addOnSuccessListener {
            Toast.makeText(this, "Food added successfully", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDatePicker() {
        DatePickerDialog(this, { _, y, m, d ->
            selectedDate.set(y, m, d)
            updateDateDisplay()
            startRealTimeMealsListener()
        }, selectedDate.get(Calendar.YEAR), selectedDate.get(Calendar.MONTH), selectedDate.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun updateDateDisplay() {
        val sdf = SimpleDateFormat("EEEE, MMM dd", Locale.getDefault())
        dateTextView.text = sdf.format(selectedDate.time)
    }

    private fun loadUserGoals() {
        val userId = auth.currentUser?.uid ?: return
        firestore.collection("users").document(userId).collection("goals").document("current")
            .get().addOnSuccessListener { doc ->
                if (doc.exists()) {
                    dailyCalorieGoal = (doc.get("dailyCalories") as? Number)?.toInt() ?: 2000
                    updateUI()
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
                    var total = 0
                    for (doc in snapshots) {
                        total += (doc.get("calories") as? Number)?.toInt() ?: 0
                    }
                    caloriesConsumed = total
                    updateUI()
                }
            }
    }

    private fun updateUI() {
        caloriesConsumedTextView.text = caloriesConsumed.toString()
        caloriesGoalTextView.text = "of $dailyCalorieGoal kcal"
        val progress = if (dailyCalorieGoal > 0) (caloriesConsumed * 100 / dailyCalorieGoal) else 0
        caloriesProgressCircle.setProgress(progress.coerceIn(0, 100), true)
    }

    override fun onDestroy() {
        super.onDestroy()
        mealsListener?.remove()
    }
}
