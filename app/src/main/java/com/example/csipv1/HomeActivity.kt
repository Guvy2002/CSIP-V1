package com.example.csipv1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton

class HomeActivity : BaseActivity() {

    override val layoutId: Int = R.layout.activity_home
    override val bottomNavigationViewId: Int = R.id.bottom_navigation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val settingsButton: ImageButton = findViewById(R.id.btn_settings)
        val addMealButton: Button = findViewById(R.id.btn_add_meal)
        val viewWorkoutsButton: Button = findViewById(R.id.btn_log_workout)

        settingsButton.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        addMealButton.setOnClickListener {
            val intent = Intent(this, CalorieTrackerActivity::class.java)
            startActivity(intent)
        }

        viewWorkoutsButton.setOnClickListener {
            val intent = Intent(this, WorkoutActivity::class.java)
            startActivity(intent)
        }
    }
}
