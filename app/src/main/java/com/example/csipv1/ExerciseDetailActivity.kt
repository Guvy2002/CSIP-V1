package com.example.csipv1

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class ExerciseDetailActivity : BaseActivity() {

    private lateinit var exercise: Exercise

    @SuppressLint("SetJavaScriptEnabled", "InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_detail)

        val exerciseId = intent.getIntExtra("EXERCISE_ID", -1)
        if (exerciseId == -1) {
            Toast.makeText(this, "Exercise not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val fetchedExercise = WorkoutData.getExerciseById(exerciseId)
        if (fetchedExercise == null) {
            Toast.makeText(this, "Exercise not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        exercise = fetchedExercise

        val titleTextView = findViewById<TextView>(R.id.tv_exercise_detail_name)
        val muscleTextView = findViewById<TextView>(R.id.tv_exercise_detail_muscle)
        val setsRepsTextView = findViewById<TextView>(R.id.tv_exercise_detail_sets_reps)
        val instructionsContainer = findViewById<LinearLayout>(R.id.steps_container)
        val closeButton = findViewById<ImageButton>(R.id.btn_back_exercise)
        val playButton = findViewById<ImageButton>(R.id.btn_play_video)
        val videoView = findViewById<WebView>(R.id.webview_exercise_video)
        val videoContainer = findViewById<View>(R.id.video_container)

        titleTextView.text = exercise.name
        muscleTextView.text = exercise.muscleGroup
        setsRepsTextView.text = "${exercise.sets} Sets × ${exercise.reps} Reps"

        populateInstructions(instructionsContainer, exercise.instructions)

        closeButton.setOnClickListener {
            finish()
        }

        playButton.setOnClickListener {
            val url = exercise.videoUrl

            if (url.isBlank()) {
                Toast.makeText(this, "Video coming soon", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            videoContainer.visibility = View.VISIBLE

            videoView.settings.javaScriptEnabled = true
            videoView.settings.loadWithOverviewMode = true
            videoView.settings.useWideViewPort = true
            videoView.webChromeClient = WebChromeClient()

            val videoId = extractYouTubeId(url)
            if (videoId != null) {
                val html = getYouTubeEmbedHtml(videoId)
                videoView.loadData(html, "text/html", "utf-8")
                playButton.visibility = View.GONE
            } else {
                Toast.makeText(this, "Could not load video", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun populateInstructions(container: LinearLayout, instructions: List<String>) {
        container.removeAllViews()

        instructions.forEachIndexed { index, instructionText ->
            val stepView = LayoutInflater.from(this)
                .inflate(R.layout.item_exercise_step, container, false)

            stepView.findViewById<TextView>(R.id.tv_step_number).text = "Step ${index + 1}"
            stepView.findViewById<TextView>(R.id.tv_step_text).text = instructionText

            container.addView(stepView)
        }
    }

    private fun getYouTubeEmbedHtml(videoId: String): String {
        return """
            <html>
                <body style="margin:0;padding:0;background-color:black;">
                    <iframe
                        width="100%"
                        height="100%"
                        src="https://www.youtube.com/embed/$videoId?autoplay=1&rel=0"
                        frameborder="0"
                        allowfullscreen>
                    </iframe>
                </body>
            </html>
        """.trimIndent()
    }

    private fun extractYouTubeId(url: String): String? {
        val pattern = "(?<=v=|v/|vi=|vi/|youtu.be/|embed/|live/|shorts/)[a-zA-Z0-9_-]{11}"
        return Regex(pattern).find(url)?.value
    }
}