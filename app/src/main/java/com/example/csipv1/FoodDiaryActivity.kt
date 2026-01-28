package com.example.csipv1

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.progressindicator.CircularProgressIndicator
import java.text.SimpleDateFormat
import java.util.*

class FoodDiaryActivity : AppCompatActivity() {

    // UI Components
    private lateinit var dateTextView: TextView
    private lateinit var caloriesConsumedTextView: TextView
    private lateinit var caloriesGoalTextView: TextView
    private lateinit var caloriesProgressCircle: CircularProgressIndicator
    private lateinit var bottomNavigation: BottomNavigationView

    // Meal Cards
    private lateinit var breakfastCard: CardView
    private lateinit var lunchCard: CardView
    private lateinit var dinnerCard: CardView
    private lateinit var snacksCard: CardView

    // Data
    private var currentDate: Calendar = Calendar.getInstance()
    private val dailyCalorieGoal = 2400
    private var totalCaloriesConsumed = 0

    // Meal data storage (In a real app, this would be a database)
    private val breakfastMeals = mutableListOf<FoodItem>()
    private val lunchMeals = mutableListOf<FoodItem>()
    private val dinnerMeals = mutableListOf<FoodItem>()
    private val snackMeals = mutableListOf<FoodItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calorietracker)

        initializeViews()
        setupClickListeners()
        updateUI()
    }

    private fun initializeViews() {
        // Main views
        dateTextView = findViewById(R.id.text_date)
        caloriesConsumedTextView = findViewById(R.id.text_calories_consumed)
        caloriesGoalTextView = findViewById(R.id.text_calories_goal)
        caloriesProgressCircle = findViewById(R.id.calories_progress_circle)
        bottomNavigation = findViewById(R.id.bottom_navigation)

        // Meal cards
        breakfastCard = findViewById(R.id.card_breakfast)
        lunchCard = findViewById(R.id.card_lunch)
        dinnerCard = findViewById(R.id.card_dinner)
        snacksCard = findViewById(R.id.card_snacks)
    }

    private fun setupClickListeners() {
        // Date picker when clicking on date
        dateTextView.setOnClickListener {
            showDatePicker()
        }

        setupMealCardClickListener(breakfastCard, "Breakfast", breakfastMeals)
        setupMealCardClickListener(lunchCard, "Lunch", lunchMeals)
        setupMealCardClickListener(dinnerCard, "Dinner", dinnerMeals)
        setupMealCardClickListener(snacksCard, "Snacks", snackMeals)

        // Bottom navigation
        bottomNavigation.selectedItemId = R.id.navigation_diary
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    true
                }
                R.id.navigation_diary -> {
                    true // Already here
                }
                R.id.navigation_exercise -> {
                    startActivity(Intent(this, WorkoutActivity::class.java))
                    true
                }
                R.id.navigation_exercise -> {
                    startActivity(Intent(this, WorkoutActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun setupMealCardClickListener(card: CardView, mealType: String, mealList: MutableList<FoodItem>) {
        card.setOnClickListener {
            showFoodSelectionDialog(mealType, mealList)
        }
    }

    private fun showDatePicker() {
        val year = currentDate.get(Calendar.YEAR)
        val month = currentDate.get(Calendar.MONTH)
        val day = currentDate.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                currentDate.set(selectedYear, selectedMonth, selectedDay)
                updateUI()
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    private fun showFoodSelectionDialog(mealType: String, mealList: MutableList<FoodItem>) {
        // In a real app, you'd show a more complex dialog to add/edit food
        val newFood = FoodItem("Sample Food", 250) // Placeholder
        mealList.add(newFood)
        totalCaloriesConsumed += newFood.calories
        updateUI()
    }

    private fun updateUI() {
        // Update date
        val dateFormat = SimpleDateFormat("EEEE, MMM dd", Locale.getDefault())
        val formattedDate = dateFormat.format(currentDate.time)
        dateTextView.text = if (isToday()) "Today, ${formattedDate.split(", ")[1]}" else formattedDate

        caloriesConsumedTextView.text = totalCaloriesConsumed.toString()
        caloriesGoalTextView.text = "of $dailyCalorieGoal kcal"

        val progress = ((totalCaloriesConsumed.toFloat() / dailyCalorieGoal) * 100).toInt()
        caloriesProgressCircle.progress = progress.coerceIn(0, 100)

        updateMealCard(breakfastCard, breakfastMeals)
        updateMealCard(lunchCard, lunchMeals)
        updateMealCard(dinnerCard, dinnerMeals)
        updateMealCard(snacksCard, snackMeals)
    }

    private fun updateMealCard(card: CardView, meals: MutableList<FoodItem>) {
        val caloriesTextView = card.findViewById<TextView>(R.id.text_meal_calories)
        val totalMealCalories = meals.sumOf { it.calories }
        caloriesTextView?.text = "$totalMealCalories kcal"
    }

    private fun isToday(): Boolean {
        val today = Calendar.getInstance()
        return currentDate.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                currentDate.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)
    }
}


data class FoodItem(
    val name: String,
    val calories: Int
)
