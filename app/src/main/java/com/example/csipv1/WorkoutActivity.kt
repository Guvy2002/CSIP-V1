package com.example.csipv1

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.random.Random

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

        setupWorkoutCard(R.id.card_chest, R.id.btn_chest, "Chest")
        setupWorkoutCard(R.id.card_back, R.id.btn_back, "Back")
        setupWorkoutCard(R.id.card_legs, R.id.btn_legs, "Legs")
        setupWorkoutCard(R.id.card_shoulders, R.id.btn_shoulders, "Shoulders")
        setupWorkoutCard(R.id.card_arms, R.id.btn_arms, "Arms")

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

    private fun setupWorkoutCard(cardId: Int, buttonId: Int, category: String) {
        val action = View.OnClickListener { openWorkoutDetail(category) }
        findViewById<MaterialCardView>(cardId).setOnClickListener(action)
        findViewById<MaterialButton>(buttonId).setOnClickListener(action)
    }

    private fun navigateTo(activityClass: Class<*>) {
        if (this::class.java == activityClass) return
        val intent = Intent(this, activityClass)
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        startActivity(intent)
        overridePendingTransition(0, 0)
    }

    private fun showDatePicker() {
        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                selectedDate.set(year, month, dayOfMonth)
                updateDateDisplay()
            },
            selectedDate.get(Calendar.YEAR),
            selectedDate.get(Calendar.MONTH),
            selectedDate.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun updateDateDisplay() {
        textDate.text = SimpleDateFormat("EEEE, MMM dd", Locale.getDefault()).format(selectedDate.time)
    }

    private fun openWorkoutDetail(category: String) {
        val intent = Intent(this, WorkoutDetailActivity::class.java)
        intent.putExtra("WORKOUT_CATEGORY", category)
        intent.putExtra(
            "SELECTED_DATE",
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate.time)
        )
        startActivity(intent)
    }
}