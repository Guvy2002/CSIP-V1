package com.example.csipv1

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class GoalsActivity : AppCompatActivity() {

    private lateinit var startingWeightInput: EditText
    private lateinit var currentWeightInput: EditText
    private lateinit var heightInput: EditText
    private lateinit var weeklyGoalInput: EditText
    private lateinit var activityLevelSpinner: Spinner
    private lateinit var saveButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goals)

        startingWeightInput = findViewById(R.id.starting_weight_input)
        currentWeightInput = findViewById(R.id.current_weight_input)
        heightInput = findViewById(R.id.height_input)
        weeklyGoalInput = findViewById(R.id.weekly_goal_input)
        activityLevelSpinner = findViewById(R.id.activity_level_spinner)
        saveButton = findViewById(R.id.save_goals_btn)

        ArrayAdapter.createFromResource(
            this,
            R.array.activity_levels,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            activityLevelSpinner.adapter = adapter
        }

        saveButton.setOnClickListener {
            val startingWeight = startingWeightInput.text.toString().trim()
            val currentWeight = currentWeightInput.text.toString().trim()
            val height = heightInput.text.toString().trim()
            val weeklyGoal = weeklyGoalInput.text.toString().trim()

            if (startingWeight.isEmpty() || currentWeight.isEmpty() || height.isEmpty() || weeklyGoal.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // In a real app, you would save the goal data to Firebase here.
            Toast.makeText(this, "Goals saved successfully!", Toast.LENGTH_SHORT).show()

            // Get the username passed from the previous activity
            val username = intent.getStringExtra("USER_NAME")

            // Navigate to the HomeActivity
            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra("USER_NAME", username) // Pass the username along
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
