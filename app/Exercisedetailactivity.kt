package com.example.csipv1

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class ExerciseDetailActivity : AppCompatActivity() {

    private var currentSet = 1
    private lateinit var exerciseName: String
    private var totalSets = 3
    private var repsPerSet = 12
    private lateinit var instructions: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_detail)

        // Get data from intent
        exerciseName = intent.getStringExtra("EXERCISE_NAME") ?: "Exercise"
        totalSets = intent.getIntExtra("EXERCISE_SETS", 3)
        repsPerSet = intent.getIntExtra("EXERCISE_REPS", 12)
        instructions = intent.getStringArrayListExtra("EXERCISE_INSTRUCTIONS") ?: arrayListOf()

        // Find views
        val closeButton = findViewById<ImageButton>(R.id.btn_close)
        val exerciseTitle = findViewById<TextView>(R.id.exercise_title)
        val exerciseImage = findViewById<ImageView>(R.id.exercise_image)
        val playVideoButton = findViewById<ImageButton>(R.id.btn_play_video)
        val setInfo = findViewById<TextView>(R.id.set_info)
        val repsCount = findViewById<TextView>(R.id.reps_count)
        val nextUpText = findViewById<TextView>(R.id.next_up_text)
        val instructionsContainer = findViewById<LinearLayout>(R.id.instructions_container)
        val startButton = findViewById<MaterialButton>(R.id.btn_start_set)

        // Set initial values
        exerciseTitle.text = exerciseName
        updateSetInfo(setInfo, repsCount, nextUpText, startButton)

        // Populate instructions
        populateInstructions(instructionsContainer)

        // Close button
        closeButton.setOnClickListener {
            finish()
        }

        // Play video button (placeholder - implement video playback as needed)
        playVideoButton.setOnClickListener {
            // TODO: Implement video playback
            // For now, just hide the play button
            playVideoButton.visibility = View.GONE
        }

        // Start button
        startButton.setOnClickListener {
            // Move to next set or finish
            if (currentSet < totalSets) {
                currentSet++
                updateSetInfo(setInfo, repsCount, nextUpText, startButton)
            } else {
                // Exercise complete
                finish()
            }
        }
    }

    /**
     * Updates the set information, reps count, and button text
     */
    private fun updateSetInfo(
        setInfo: TextView,
        repsCount: TextView,
        nextUpText: TextView,
        startButton: MaterialButton
    ) {
        setInfo.text = "SET $currentSet OF $totalSets"
        repsCount.text = "$repsPerSet REPS"

        if (currentSet < totalSets) {
            nextUpText.text = "Next up: Set ${currentSet + 1}"
            startButton.text = "START SET $currentSet"
        } else {
            nextUpText.text = "Last set!"
            startButton.text = "COMPLETE EXERCISE"
        }
    }

    /**
     * Populates the instructions list
     */
    private fun populateInstructions(container: LinearLayout) {
        instructions.forEachIndexed { index, instruction ->
            val instructionView = createInstructionView(index + 1, instruction)
            container.addView(instructionView)
        }
    }

    /**
     * Creates a single instruction view with number and text
     */
    private fun createInstructionView(number: Int, text: String): View {
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.HORIZONTAL
        layout.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        val layoutParams = layout.layoutParams as LinearLayout.LayoutParams
        layoutParams.bottomMargin = dpToPx(16)

        // Number circle
        val numberView = TextView(this)
        numberView.text = number.toString()
        numberView.textSize = 16f
        numberView.setTypeface(null, android.graphics.Typeface.BOLD)
        numberView.setTextColor(getColor(android.R.color.white))
        numberView.setBackgroundResource(android.R.drawable.ic_menu_add)
        numberView.background.setTint(getColor(R.color.teal_200))

        val numberParams = LinearLayout.LayoutParams(
            dpToPx(32),
            dpToPx(32)
        )
        numberParams.marginEnd = dpToPx(16)
        numberView.layoutParams = numberParams
        numberView.gravity = android.view.Gravity.CENTER

        // Instruction text
        val textView = TextView(this)
        textView.text = text
        textView.textSize = 16f
        textView.setTextColor(getColor(android.R.color.black))
        textView.layoutParams = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            1f
        )

        layout.addView(numberView)
        layout.addView(textView)

        return layout
    }

    /**
     * Convert dp to pixels
     */
    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }
}