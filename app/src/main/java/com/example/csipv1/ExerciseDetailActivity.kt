package com.example.csipv1

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class ExerciseDetailActivity : AppCompatActivity() {

    private lateinit var exercise: Exercise

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercisedetail)

        val exerciseId = intent.getIntExtra("EXERCISE_ID", -1)
        if (exerciseId == -1) {
            finish()
            return
        }

        val fetchedExercise = WorkoutData.getExerciseById(exerciseId)
        if (fetchedExercise == null) {
            finish()
            return
        }
        exercise = fetchedExercise

        val titleTextView = findViewById<TextView>(R.id.exercise_title)
        val setInfoTextView = findViewById<TextView>(R.id.set_info)
        val repsCountTextView = findViewById<TextView>(R.id.reps_count)
        val instructionsContainer = findViewById<LinearLayout>(R.id.instructions_container)
        val startButton = findViewById<MaterialButton>(R.id.btn_start_set)
        val closeButton = findViewById<ImageButton>(R.id.btn_close)

        // ✅ These MUST exist in your XML
        val playButton = findViewById<ImageButton>(R.id.btn_play_video)
        val videoView = findViewById<VideoView>(R.id.exercise_video)

        titleTextView.text = exercise.name
        setInfoTextView.text = "GOAL: ${exercise.sets} SETS"
        repsCountTextView.text = "${exercise.reps} REPS"
        populateInstructions(instructionsContainer, exercise.instructions)

        closeButton.setOnClickListener { finish() }

        // ✅ Play video button works immediately
        playButton.setOnClickListener {
            val url = exercise.videoUrl

            if (url.isBlank()) {
                Toast.makeText(this, "Video coming soon", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            videoView.visibility = View.VISIBLE
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

        startButton.setOnClickListener {
            // your start set logic here (timer etc)
        }
    }

    private fun populateInstructions(container: LinearLayout, instructions: List<String>) {
        container.removeAllViews()

        if (instructions.isEmpty()) {
            val noInstructionsView = TextView(this)
            noInstructionsView.text = "No specific instructions available."
            noInstructionsView.setTextColor(resources.getColor(android.R.color.darker_gray, null))
            container.addView(noInstructionsView)
            return
        }

        instructions.forEachIndexed { index, instructionText ->
            val instructionView = LayoutInflater.from(this)
                .inflate(R.layout.activity_intruc_num, container, false) as LinearLayout

            instructionView.findViewById<TextView>(R.id.instruction_number).text = "${index + 1}"
            instructionView.findViewById<TextView>(R.id.instruction_text).text = instructionText

            container.addView(instructionView)
        }
    }
}
