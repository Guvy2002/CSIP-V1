package com.example.csipv1

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.random.Random

class WorkoutActivity : BaseActivity() {

    private lateinit var textDate: TextView
    private lateinit var btnCalendar: ImageButton
    private lateinit var containerCompletedExercises: LinearLayout
    private lateinit var textPlaceholder: TextView
    private lateinit var textTotalCalories: TextView
    private lateinit var bottomNavigation: BottomNavigationView
    private var selectedDate: Calendar = Calendar.getInstance()
    
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private var completionListener: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workouts)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        initializeViews()
        setupListeners()
        updateDateDisplay()
        startCompletedWorkoutsListener()
    }

    override fun onResume() {
        super.onResume()
        bottomNavigation.selectedItemId = R.id.navigation_exercise
    }

    private fun initializeViews() {
        textDate = findViewById(R.id.text_workout_date)
        btnCalendar = findViewById(R.id.btn_workout_calendar)
        containerCompletedExercises = findViewById(R.id.container_completed_exercises)
        textPlaceholder = findViewById(R.id.text_completed_placeholder)
        textTotalCalories = findViewById(R.id.text_total_calories_burnt)
        bottomNavigation = findViewById(R.id.bottom_navigation)

        val tipTextView: TextView = findViewById(R.id.text_workout_tip)
        val workoutTips = resources.getStringArray(R.array.workout_tips)
        if (workoutTips.isNotEmpty()) {
            tipTextView.text = workoutTips[Random.nextInt(workoutTips.size)]
        }
    }

    private fun startCompletedWorkoutsListener() {
        val userId = auth.currentUser?.uid ?: return
        val dateString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate.time)

        completionListener?.remove()
        completionListener = firestore.collection("users").document(userId)
            .collection("completed_exercises")
            .whereEqualTo("date", dateString)
            .addSnapshotListener { snapshots, _ ->
                containerCompletedExercises.removeAllViews()
                
                if (snapshots != null && !snapshots.isEmpty) {
                    val completedIds = snapshots.mapNotNull { it.getString("exerciseId")?.toIntOrNull() }
                    
                    if (completedIds.isNotEmpty()) {
                        textPlaceholder.visibility = View.GONE
                        val totalCals = completedIds.size * 40 
                        textTotalCalories.text = "$totalCals kcal"
                        textTotalCalories.visibility = View.VISIBLE
                        findViewById<View>(R.id.layout_calorie_summary).visibility = View.VISIBLE

                        completedIds.mapNotNull { id -> WorkoutData.getExerciseById(id) }.forEach { exercise ->
                            addExerciseToContainer(exercise.name)
                        }
                    } else {
                        showNoDataState()
                    }
                } else {
                    showNoDataState()
                }
            }
    }

    private fun showNoDataState() {
        textPlaceholder.visibility = View.VISIBLE
        textTotalCalories.visibility = View.GONE
        findViewById<View>(R.id.layout_calorie_summary).visibility = View.GONE
        containerCompletedExercises.addView(textPlaceholder)
    }

    private fun addExerciseToContainer(exerciseName: String) {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            setPadding(0, 4, 0, 4)
            gravity = Gravity.CENTER_VERTICAL
        }

        val tickView = TextView(this).apply {
            text = "✔️"
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            setPadding(0, 0, 12, 0)
        }

        val nameView = TextView(this).apply {
            text = exerciseName
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            setTextColor(resources.getColor(android.R.color.white, null))
            typeface = Typeface.DEFAULT
            alpha = 0.9f
        }

        row.addView(tickView)
        row.addView(nameView)
        containerCompletedExercises.addView(row)
    }

    private fun setupListeners() {
        textDate.setOnClickListener { showDatePicker() }
        btnCalendar.setOnClickListener { showDatePicker() }

        findViewById<MaterialCardView>(R.id.card_view_plans).setOnClickListener {
            startActivity(Intent(this, TrainingPlansActivity::class.java))
        }

        findViewById<MaterialCardView>(R.id.card_view_upper_lower_plans).setOnClickListener {
            startActivity(Intent(this, UpperLowerPlansActivity::class.java))
        }

        setupWorkoutCard(R.id.card_chest, R.id.btn_chest, "Chest")
        setupWorkoutCard(R.id.card_back, R.id.btn_back, "Back")
        setupWorkoutCard(R.id.card_legs, R.id.btn_legs, "Legs")
        setupWorkoutCard(R.id.card_shoulders, R.id.btn_shoulders, "Shoulders")
        setupWorkoutCard(R.id.card_arms, R.id.btn_arms, "Arms")
        setupWorkoutCard(R.id.card_abs, R.id.btn_abs, "Abs")

        // Recovery Section Listeners
        findViewById<MaterialCardView>(R.id.card_stretching).setOnClickListener { showStretchingInfo() }
        findViewById<MaterialCardView>(R.id.card_cooldown).setOnClickListener { showCooldownInfo() }

        bottomNavigation.setOnItemSelectedListener { item ->
            if (item.itemId == R.id.navigation_exercise) return@setOnItemSelectedListener true
            val target = when (item.itemId) {
                R.id.navigation_home -> HomeActivity::class.java
                R.id.navigation_diary -> CalorieTrackerActivity::class.java
                R.id.navigation_community -> CommunityActivity::class.java
                else -> null
            }
            target?.let {
                val intent = Intent(this, it)
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                startActivity(intent)
                @Suppress("DEPRECATION")
                overridePendingTransition(0, 0)
                true
            } ?: false
        }
    }

    private fun showStretchingInfo() {
        val info = """
            Stretching improves flexibility and range of motion.
            
            1. Static Stretching: Hold a stretch for 30s. Best after a workout.
            2. Dynamic Stretching: Active movements (e.g., leg swings). Best before a workout.
            3. PNF Stretching: Contracting and relaxing muscles. For advanced flexibility.
        """.trimIndent()
        
        AlertDialog.Builder(this)
            .setTitle("Stretching Guide")
            .setMessage(info)
            .setPositiveButton("Got it", null)
            .show()
    }

    private fun showCooldownInfo() {
        val info = """
            A cool down gradually lowers your heart rate and prevents blood pooling.
            
            1. Light Cardio: 5 mins of walking or slow cycling.
            2. Deep Breathing: Helps regulate your nervous system.
            3. Foam Rolling: Releases muscle tension and improves blood flow.
        """.trimIndent()

        AlertDialog.Builder(this)
            .setTitle("Cool Down Guide")
            .setMessage(info)
            .setPositiveButton("Got it", null)
            .show()
    }

    private fun setupWorkoutCard(cardId: Int, buttonId: Int, category: String) {
        val action = View.OnClickListener { openWorkoutDetail(category) }
        findViewById<MaterialCardView>(cardId).setOnClickListener(action)
        findViewById<MaterialButton>(buttonId).setOnClickListener(action)
    }

    private fun showDatePicker() {
        DatePickerDialog(this, { _, year, month, dayOfMonth ->
            selectedDate.set(year, month, dayOfMonth)
            updateDateDisplay()
            startCompletedWorkoutsListener()
        }, selectedDate.get(Calendar.YEAR), selectedDate.get(Calendar.MONTH), selectedDate.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun updateDateDisplay() {
        textDate.text = SimpleDateFormat("EEEE, MMM dd", Locale.getDefault()).format(selectedDate.time)
    }

    private fun openWorkoutDetail(category: String) {
        val intent = Intent(this, WorkoutDetailActivity::class.java)
        intent.putExtra("WORKOUT_CATEGORY", category)
        intent.putExtra("SELECTED_DATE", SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate.time))
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        completionListener?.remove()
    }
}
