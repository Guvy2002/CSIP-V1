package com.example.csipv1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlin.random.Random

/**
 * Optimized Workout Activity extending BaseActivity for faster theme/setting application.
 */
class WorkoutActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workouts)

        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
        val tipTextView: TextView = findViewById(R.id.text_workout_tip)

        // --- Set Random Workout Tip ---
        val workoutTips = resources.getStringArray(R.array.workout_tips)
        if (workoutTips.isNotEmpty()) {
            val randomTip = workoutTips[Random.nextInt(workoutTips.size)]
            tipTextView.text = randomTip
        }

        // Find all workout buttons
        val btnViewChest = findViewById<Button>(R.id.btn_view_chest)
        val btnViewBack = findViewById<Button>(R.id.btn_view_back)
        val btnViewLeg = findViewById<Button>(R.id.btn_view_leg)
        val btnViewShoulder = findViewById<Button>(R.id.btn_view_shoulder)
        val btnViewArms = findViewById<Button>(R.id.btn_view_arms)

        // Set click listeners for each workout button
        btnViewChest.setOnClickListener {
            openWorkoutDetail(1) // Chest Workout
        }
        btnViewBack.setOnClickListener {
            openWorkoutDetail(2) // Back Workout
        }
        btnViewLeg.setOnClickListener {
            openWorkoutDetail(3) // Leg Workout
        }
        btnViewShoulder.setOnClickListener {
            openWorkoutDetail(4) // Shoulder Workout
        }
        btnViewArms.setOnClickListener {
            openWorkoutDetail(5) // Arms Workout
        }

        // Bottom navigation
        bottomNavigation.selectedItemId = R.id.navigation_exercise
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    true
                }
                R.id.navigation_diary -> {
                    startActivity(Intent(this, CalorieTrackerActivity::class.java))
                    true
                }
                R.id.navigation_community -> {
                    startActivity(Intent(this, CommunityActivity::class.java))
                    true
                }
                R.id.navigation_exercise -> {
                    // Already on the Workouts screen
                    true
                }
                else -> false
            }
        }
    }

    /**
     * Opens the workout detail activity for the selected workout
     */
    private fun openWorkoutDetail(workoutId: Int) {
        val intent = Intent(this, WorkoutDetailActivity::class.java)
        intent.putExtra("WORKOUT_ID", workoutId)
        startActivity(intent)
    }
}
