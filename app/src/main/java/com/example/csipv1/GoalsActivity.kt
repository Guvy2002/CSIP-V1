package com.example.csipv1

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity

class GoalsActivity : AppCompatActivity() {

    private lateinit var startingWeightInput: EditText
    private lateinit var currentWeightInput: EditText
    private lateinit var weeklyGoalInput: EditText
    private lateinit var activityLevelSpinner: Spinner
    private lateinit var saveButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goals)

        startingWeightInput = findViewById(R.id.starting_weight_input)
        currentWeightInput = findViewById(R.id.current_weight_input)
        weeklyGoalInput = findViewById(R.id.weekly_goal_input)
        activityLevelSpinner = findViewById(R.id.activity_level_spinner)
        saveButton = findViewById(R.id.save_goals_btn)

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            this,
            R.array.activity_levels,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            activityLevelSpinner.adapter = adapter
        }

        saveButton.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
        }
    }
}
