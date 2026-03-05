package com.example.csipv1

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.card.MaterialCardView
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

/**
 * Finalized Workout Activity with direct card-based navigation.
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

        // --- DIRECT CARD NAVIGATION ---
        // We attach listeners only to the cards. Buttons are set to non-clickable in XML.
        
        findViewById<MaterialCardView>(R.id.workout_1_card).setOnClickListener { 
            Log.d("WORKOUT_CLICK", "Chest Card Clicked")
            openWorkoutDetail("Chest") 
        }
        
        findViewById<MaterialCardView>(R.id.workout_2_card).setOnClickListener { 
            Log.d("WORKOUT_CLICK", "Back Card Clicked")
            openWorkoutDetail("Back") 
        }
        
        findViewById<MaterialCardView>(R.id.workout_3_card).setOnClickListener { 
            Log.d("WORKOUT_CLICK", "Leg Card Clicked")
            openWorkoutDetail("Leg") 
        }
        
        findViewById<MaterialCardView>(R.id.workout_4_card).setOnClickListener { 
            Log.d("WORKOUT_CLICK", "Shoulder Card Clicked")
            openWorkoutDetail("Shoulder") 
        }
        
        findViewById<MaterialCardView>(R.id.workout_5_card).setOnClickListener { 
            Log.d("WORKOUT_CLICK", "Arms Card Clicked")
            openWorkoutDetail("Arms") 
        }

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
        Toast.makeText(this, "Opening $category Plan...", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, WorkoutDetailActivity::class.java)
        intent.putExtra("WORKOUT_CATEGORY", category)
        intent.putExtra("SELECTED_DATE", SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate.time))
        startActivity(intent)
    }
}
