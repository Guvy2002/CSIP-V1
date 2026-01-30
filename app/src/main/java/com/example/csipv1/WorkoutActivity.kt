package com.example.csipv1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class WorkoutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workouts)

        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)

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
                    finish()
                    true
                }
                R.id.navigation_diary -> {
                    startActivity(Intent(this, FoodDiaryActivity::class.java))
                    finish()
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
