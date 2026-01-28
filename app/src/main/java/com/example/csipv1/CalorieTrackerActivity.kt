package com.example.csipv1

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class CalorieTrackerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calorietracker)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)

        // --- Setup Bottom Navigation ---
        bottomNavigationView.selectedItemId = R.id.navigation_diary // Set the current item
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    true
                }
                R.id.navigation_diary -> {
                    // Already on the Diary screen
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
}
