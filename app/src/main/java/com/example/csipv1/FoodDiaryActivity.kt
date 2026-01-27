package com.example.csipv1

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
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
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // Navigate to home
                    true
                }
                R.id.nav_tracker -> {
                    // Already on tracker
                    true
                }
                R.id.nav_progress -> {
                    // Navigate to progress
                    true
                }
                R.id.nav_profile -> {
                    // Navigate to profile
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
        // Sample food database (In production, this would come from a real database)
        val availableFoods = getSampleFoodDatabase(mealType)

        val foodNames = availableFoods.map { "${it.name} (${it.calories} kcal)" }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("Add $mealType")
            .setItems(foodNames) { dialog, which ->
                val selectedFood = availableFoods[which]
                mealList.add(selectedFood)
                totalCaloriesConsumed += selectedFood.calories
                updateUI()
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun getSampleFoodDatabase(mealType: String): List<FoodItem> {
        // database needed
        return when (mealType.lowercase()) {
            "breakfast" -> listOf(
                FoodItem("Oatmeal with Berries", 250),
                FoodItem("Scrambled Eggs (2)", 180),
                FoodItem("Greek Yogurt with Honey", 150),
                FoodItem("Avocado Toast", 300),
                FoodItem("Protein Smoothie", 220),
                FoodItem("Pancakes (3)", 350),
                FoodItem("Cereal with Milk", 200)
            )
            "lunch" -> listOf(
                FoodItem("Grilled Chicken Salad", 350),
                FoodItem("Turkey Sandwich", 400),
                FoodItem("Quinoa Bowl", 450),
                FoodItem("Pasta with Vegetables", 500),
                FoodItem("Sushi Roll (8 pieces)", 300),
                FoodItem("Chicken Wrap", 380),
                FoodItem("Vegetable Soup", 200)
            )
            "dinner" -> listOf(
                FoodItem("Grilled Salmon with Rice", 550),
                FoodItem("Steak with Vegetables", 600),
                FoodItem("Chicken Stir Fry", 480),
                FoodItem("Vegetarian Pizza (2 slices)", 500),
                FoodItem("Beef Tacos (2)", 450),
                FoodItem("Pasta Carbonara", 650),
                FoodItem("Roasted Chicken Breast", 400)
            )
            "snacks" -> listOf(
                FoodItem("Apple", 95),
                FoodItem("Protein Bar", 200),
                FoodItem("Mixed Nuts (handful)", 170),
                FoodItem("String Cheese", 80),
                FoodItem("Baby Carrots with Hummus", 120),
                FoodItem("Dark Chocolate (2 squares)", 100),
                FoodItem("Rice Cakes (2)", 70)
            )
            else -> emptyList()
        }
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