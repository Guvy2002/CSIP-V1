package com.example.csipv1

class DiaryActivity {
}

package com.example.csipv1

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class WorkoutActivity {
}

class WorkoutsActivity : AppCompatActivity() {

    // Keep track of the current week number
    private var currentWeek = 1
    private val maxWeeks = 54

    private lateinit var weekNumberTextView: TextView
    private lateinit var prevWeekButton: ImageButton
    private lateinit var nextWeekButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workouts)

        // Initialize the views from your XML
        weekNumberTextView = findViewById(R.id.text_week_number)
        prevWeekButton = findViewById(R.id.button_previous_week)
        nextWeekButton = findViewById(R.id.button_next_week)

        // Set up the click listeners for the buttons
        prevWeekButton.setOnClickListener {
            if (currentWeek > 1) {
                currentWeek--
                updateWeekView()
            }
        }

        nextWeekButton.setOnClickListener {
            if (currentWeek < maxWeeks) {
                currentWeek++
                updateWeekView()
            }
        }

        // Initialize the view for the first time
        updateWeekView()
    }

    private fun updateWeekView() {
        // Update the text to show the current week
        weekNumberTextView.text = "Week $currentWeek"

        // load the new workouts
        prevWeekButton.isEnabled = currentWeek > 1
        nextWeekButton.isEnabled = currentWeek < maxWeeks
    }

    private fun loadWorkoutsForWeek(week: Int) {
        // TODO: Implement the logic content of your workout CardViews

}