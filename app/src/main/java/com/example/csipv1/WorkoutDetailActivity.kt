package com.example.csipv1

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.bottomnavigation.BottomNavigationView

class WorkoutDetailActivity : AppCompatActivity() {

    private lateinit var workout: Workout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workoutdetails)

        // Get workout ID from intent
        val workoutId = intent.getIntExtra("WORKOUT_ID", 1)
        // FIX: WorkoutData now exists
        workout = WorkoutData.getWorkoutById(workoutId) ?: WorkoutData.getAllWorkouts()[0]

        // Setup views
        val titleTextView = findViewById<TextView>(R.id.workout_detail_title)
        val exerciseContainer = findViewById<LinearLayout>(R.id.exercise_list_container)
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // Set title from workout data
        titleTextView.text = workout.name

        // Populate exercise list
        populateExercises(exerciseContainer)

        // FIX: Commenting out icon size logic as the helper classes do not exist yet.
        // applyIconSizes(bottomNavigation)

        // Bottom Navigation
        // FIX: Use the correct IDs from your menu file
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
                    finish() // Go back to workout list
                    true
                }
                else -> false
            }
        }
    }

    private fun populateExercises(container: LinearLayout) {
        workout.exercises.forEach { exercise ->
            val cardView = createExerciseCard(container, exercise)
            container.addView(cardView)
        }
    }

    private fun createExerciseCard(container: ViewGroup, exercise: Exercise): CardView {
        // Inflate the reusable exercise card layout
        val cardView = LayoutInflater.from(this)
            .inflate(R.layout.activity_exercise_card, container, false) as CardView

        // Find the views inside the inflated layout
        val nameTextView = cardView.findViewById<TextView>(R.id.exercise_name)
        val setsTextView = cardView.findViewById<TextView>(R.id.exercise_sets)
        val imageView = cardView.findViewById<ImageView>(R.id.exercise_image)

        // Set the data
        nameTextView.text = exercise.name
        setsTextView.text = "${exercise.sets} sets x ${exercise.reps} reps"
        // You can set a placeholder or a real image here
        // imageView.setImageResource(exercise.imageResId)

        // Set click listener to open exercise detail
        cardView.setOnClickListener {
            openExerciseDetail(exercise)
        }

        return cardView
    }

    private fun openExerciseDetail(exercise: Exercise) {
        val intent = Intent(this, ExerciseDetailActivity::class.java)
        intent.putExtra("EXERCISE_ID", exercise.id)
        startActivity(intent)
    }

    /*
    private fun applyIconSizes(bottomNav: BottomNavigationView) {
        val iconSizePx = getIconSizePx()
        IconSizeHelper.applyToBottomNavigation(bottomNav, iconSizePx)
    }
    */
}
