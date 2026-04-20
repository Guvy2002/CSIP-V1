package com.example.csipv1

import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.google.android.material.button.MaterialButton
import java.util.*

class ExerciseDetailActivity : BaseActivity() {

    private lateinit var exercise: Exercise
    private lateinit var timerText: TextView
    private lateinit var timerButton: MaterialButton
    private lateinit var videoView: VideoView
    private lateinit var playButton: ImageButton
    private lateinit var videoProgress: ProgressBar
    
    private var countDownTimer: CountDownTimer? = null
    private var isTimerRunning = false
    private val startTimeInMillis: Long = 90000 

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercisedetail)

        val exerciseId = intent.getIntExtra("EXERCISE_ID", -1)
        val fetchedExercise = WorkoutData.getExerciseById(exerciseId)

        if (fetchedExercise == null) {
            Toast.makeText(this, "Exercise not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        exercise = fetchedExercise

        initializeViews()
        populateData()
        setupListeners()
        prepareVideo()
    }

    private fun initializeViews() {
        timerText = findViewById(R.id.tv_timer_display)
        timerButton = findViewById(R.id.btn_timer_control)
        videoView = findViewById(R.id.exercise_video)
        playButton = findViewById(R.id.btn_play_video)
        // Adding a loading indicator to the layout would be good here
    }

    private fun prepareVideo() {
        if (exercise.videoUrl.isNotBlank()) {
            videoView.setVideoURI(Uri.parse(exercise.videoUrl))
            videoView.setOnPreparedListener { mp ->
                mp.isLooping = true
                // Ready to play
            }
            videoView.setOnErrorListener { _, _, _ ->
                Toast.makeText(this, "Error loading video", Toast.LENGTH_SHORT).show()
                true
            }
        }
    }

    private fun populateData() {
        findViewById<TextView>(R.id.exercise_title).text = exercise.name
        findViewById<TextView>(R.id.set_info).text = "GOAL: ${exercise.sets} SETS"
        findViewById<TextView>(R.id.reps_count).text = "${exercise.reps} REPS"
        
        val instructionsContainer = findViewById<LinearLayout>(R.id.instructions_container)
        populateInstructions(instructionsContainer, exercise.instructions)
    }

    private fun setupListeners() {
        findViewById<ImageButton>(R.id.btn_close).setOnClickListener { finish() }

        playButton.setOnClickListener { view ->
            if (exercise.videoUrl.isBlank()) {
                Toast.makeText(this, "Video coming soon", Toast.LENGTH_SHORT).show()
            } else {
                videoView.visibility = View.VISIBLE
                videoView.start()
                view.visibility = View.GONE
            }
        }

        timerButton.setOnClickListener {
            if (isTimerRunning) {
                resetTimer()
            } else {
                startTimer()
            }
        }
    }

    private fun startTimer() {
        countDownTimer = object : CountDownTimer(startTimeInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                updateTimerText(millisUntilFinished)
            }

            override fun onFinish() {
                isTimerRunning = false
                timerButton.text = "Start"
                Toast.makeText(this@ExerciseDetailActivity, "Rest Over! Next Set!", Toast.LENGTH_LONG).show()
            }
        }.start()

        isTimerRunning = true
        timerButton.text = "Reset"
    }

    private fun resetTimer() {
        countDownTimer?.cancel()
        isTimerRunning = false
        timerButton.text = "Start"
        updateTimerText(startTimeInMillis)
    }

    private fun updateTimerText(millis: Long) {
        val minutes = (millis / 1000) / 60
        val seconds = (millis / 1000) % 60
        timerText.text = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
    }

    private fun populateInstructions(container: LinearLayout, instructions: List<String>) {
        container.removeAllViews()
        instructions.forEachIndexed { index, text ->
            val stepView = LayoutInflater.from(this).inflate(R.layout.item_exercise_step, container, false)
            stepView.findViewById<TextView>(R.id.tv_step_number).text = "Step ${index + 1}"
            stepView.findViewById<TextView>(R.id.tv_step_text).text = text
            container.addView(stepView)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }
}
