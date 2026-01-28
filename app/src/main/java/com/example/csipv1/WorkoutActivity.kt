package com.example.csipv1

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class WorkoutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workouts)

        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)

        // Bottom navigation
        bottomNavigation.selectedItemId = R.id.navigation_exercise
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    true
                }
                R.id.navigation_diary -> {
                    startActivity(Intent(this, FoodDiaryActivity::class.java))
                    true
                }
                R.id.navigation_exercise -> {
                    // Already on the Exercise screen
                    true
                }

                else -> false
            }
        }
    }
}
