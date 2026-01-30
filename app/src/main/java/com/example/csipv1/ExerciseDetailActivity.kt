package com.example.csipv1

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import android.net.Uri
import android.view.View
import android.widget.Toast
import android.widget.VideoView


class ExerciseDetailActivity : AppCompatActivity() {

    private lateinit var exercise: Exercise

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercisedetail)

        // 1. Get the Exercise ID passed from the previous activity
        val exerciseId = intent.getIntExtra("EXERCISE_ID", -1)
        if (exerciseId == -1) {
            // If no ID was passed, we can't proceed. Close the activity.
            finish()
            return
        }

        // 2. Fetch the correct Exercise data using the ID from WorkoutData
        val fetchedExercise = WorkoutData.getExerciseById(exerciseId)
        if (fetchedExercise == null) {
            // If the ID is invalid (exercise not found), close the activity.
            finish()
            return
        }
        exercise = fetchedExercise

        // 3. Find all the views from your activity_exercisedetail.xml layout
        val titleTextView = findViewById<TextView>(R.id.exercise_title)
        val setInfoTextView = findViewById<TextView>(R.id.set_info)
        val repsCountTextView = findViewById<TextView>(R.id.reps_count)
        val instructionsContainer = findViewById<LinearLayout>(R.id.instructions_container)
        val startButton = findViewById<MaterialButton>(R.id.btn_start_set)
        val closeButton = findViewById<ImageButton>(R.id.btn_close)

        // 4. Populate the views with the dynamic data from the fetched exercise
        titleTextView.text = exercise.name
        setInfoTextView.text = "GOAL: ${exercise.sets} SETS" // Updated to match your layout's style
        repsCountTextView.text = "${exercise.reps} REPS"

        // 5. Dynamically create and add the instruction steps
        populateInstructions(instructionsContainer, exercise.instructions)

        // 6. Set up button listeners
        closeButton.setOnClickListener {
            finish() // This will close the current activity and go back
        }

        startButton.setOnClickListener {
            // You can add logic here for what happens when a set starts
            // e.g., start a timer.
            val playButton = findViewById<ImageButton>(R.id.btn_play_video)
            val videoView = findViewById<VideoView>(R.id.exercise_video)

            playButton.setOnClickListener {
                val url = exercise.videoUrl

                if (url.isBlank()) {
                    Toast.makeText(this, "Video coming soon", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                videoView.setVideoURI(Uri.parse(url))
                videoView.setOnPreparedListener { mp ->
                    mp.isLooping = true
                    videoView.start()
                    playButton.visibility = View.GONE
                }
                videoView.setOnErrorListener { _, what, extra ->
                    Toast.makeText(this, "Video error: $what / $extra", Toast.LENGTH_LONG).show()
                    true
                }
            }

        }
    }

    /**
     * Clears the container and dynamically adds a TextView for each instruction.
     */
    private fun populateInstructions(container: LinearLayout, instructions: List<String>) {
        container.removeAllViews() // Clear any old instructions from the template

        if (instructions.isEmpty()) {
            // Optional: Handle the case where there are no instructions
            val noInstructionsView = TextView(this)
            noInstructionsView.text = "No specific instructions available."
            noInstructionsView.setTextColor(resources.getColor(android.R.color.darker_gray, null))
            container.addView(noInstructionsView)
            return
        }

        instructions.forEachIndexed { index, instructionText ->
            // Create a new instruction step view for each instruction
            val instructionView = LayoutInflater.from(this)
                .inflate(R.layout.activity_intruc_num, container, false) as LinearLayout

            val numberTextView = instructionView.findViewById<TextView>(R.id.instruction_number)
            val textTextView = instructionView.findViewById<TextView>(R.id.instruction_text)

            numberTextView.text = "${index + 1}"
            textTextView.text = instructionText

            container.addView(instructionView)
        }
    }
}
