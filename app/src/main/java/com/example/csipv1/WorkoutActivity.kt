package com.example.csipv1

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.card.MaterialCardView
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

/**
 * Optimized Workout Activity with robust card-based navigation.
 * Clicking anywhere on a workout card will now open the breakout pages.
 */
class WorkoutActivity : BaseActivity() {

    private lateinit var textDate: TextView
    private lateinit var btnCalendar: ImageButton
    private lateinit var bottomNavigation: BottomNavigationView
    private var selectedDate: Calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workouts)

        initializeViews()
        setupListeners()
        updateDateDisplay()
    }

    override fun onResume() {
        super.onResume()
        // Ensure the correct icon is highlighted when returning to this page
        bottomNavigation.selectedItemId = R.id.navigation_exercise
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    private fun initializeViews() {
        textDate = findViewById(R.id.text_workout_date)
        btnCalendar = findViewById(R.id.btn_workout_calendar)
        bottomNavigation = findViewById(R.id.bottom_navigation)
        
        val tipTextView: TextView = findViewById(R.id.text_workout_tip)
        val workoutTips = resources.getStringArray(R.array.workout_tips)
        if (workoutTips.isNotEmpty()) {
            tipTextView.text = workoutTips[Random.nextInt(workoutTips.size)]
        }
    }

    private fun setupListeners() {
        textDate.setOnClickListener { showDatePicker() }
        btnCalendar.setOnClickListener { showDatePicker() }

        // --- Robust Navigation: Attach listeners to the entire CARDS ---
        // This solves the conflict where cards were stealing clicks from the buttons.
        
        setupCardNavigation(R.id.card_chest, R.id.btn_chest, "Chest")
        setupCardNavigation(R.id.card_back, R.id.btn_back, "Back")
        setupCardNavigation(R.id.card_legs, R.id.btn_legs, "Leg")
        setupCardNavigation(R.id.card_shoulders, R.id.btn_shoulders, "Shoulder")
        setupCardNavigation(R.id.card_arms, R.id.btn_arms, "Arms")

        bottomNavigation.setOnItemSelectedListener { item ->
            if (item.itemId == R.id.navigation_exercise) return@setOnItemSelectedListener true
            val target = when (item.itemId) {
                R.id.navigation_home -> HomeActivity::class.java
                R.id.navigation_diary -> CalorieTrackerActivity::class.java
                R.id.navigation_community -> CommunityActivity::class.java
                else -> null
            }
            target?.let {
                navigateTo(it)
                true
            } ?: false
        }
    }

    /**
     * Helper to ensure both the card and button respond to navigation.
     */
    private fun setupCardNavigation(cardId: Int, buttonId: Int, category: String) {
        val clickAction = View.OnClickListener {
            openWorkoutDetail(category)
        }
        
        findViewById<MaterialCardView>(cardId).setOnClickListener(clickAction)
        findViewById<Button>(buttonId).setOnClickListener(clickAction)
    }

    private fun navigateTo(activityClass: Class<*>) {
        if (this::class.java == activityClass) return
        val intent = Intent(this, activityClass)
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        startActivity(intent)
        overridePendingTransition(0, 0)
    }

    private fun showDatePicker() {
        val datePickerDialog = DatePickerDialog(this, { _, year, month, dayOfMonth ->
            selectedDate.set(year, month, dayOfMonth)
            updateDateDisplay()
        }, selectedDate.get(Calendar.YEAR), selectedDate.get(Calendar.MONTH), selectedDate.get(Calendar.DAY_OF_MONTH))
        datePickerDialog.show()
    }

    private fun updateDateDisplay() {
        textDate.text = SimpleDateFormat("EEEE, MMM dd", Locale.getDefault()).format(selectedDate.time)
    }

    private fun openWorkoutDetail(category: String) {
        // Confirmation Toast to verify the tap worked
        Toast.makeText(this, "Opening $category Plan...", Toast.LENGTH_SHORT).show()
        
        val intent = Intent(this, WorkoutDetailActivity::class.java)
        intent.putExtra("WORKOUT_CATEGORY", category)
        intent.putExtra("SELECTED_DATE", SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate.time))
        startActivity(intent)
    }
}
