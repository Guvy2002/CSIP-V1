package com.example.csipv1

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import java.text.SimpleDateFormat
import java.util.*

/**
 * Optimized Detail Activity for viewing specific workout plans and tracking exercise completion.
 */
class WorkoutDetailActivity : BaseActivity() {

    private lateinit var textWorkoutTitle: TextView
    private lateinit var textDate: TextView
    private lateinit var btnCalendar: ImageButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var bottomNavigation: BottomNavigationView
    
    private lateinit var adapter: ExerciseAdapter
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var completionListener: ListenerRegistration? = null
    
    private var workoutCategory: String = "Chest"
    private var selectedDateString: String = ""
    private var selectedDate: Calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workoutdetails)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Get data from intent
        workoutCategory = intent.getStringExtra("WORKOUT_CATEGORY") ?: "Chest"
        selectedDateString = intent.getStringExtra("SELECTED_DATE") ?: 
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        
        // Parse the date string into our Calendar object
        try {
            val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(selectedDateString)
            if (date != null) selectedDate.time = date
        } catch (e: Exception) { e.printStackTrace() }

        initializeViews()
        setupRecyclerView()
        setupListeners()
        updateDateDisplay()
        startRealTimeCompletionListener()
    }

    override fun onResume() {
        super.onResume()
        bottomNavigation.selectedItemId = R.id.navigation_exercise
    }

    private fun initializeViews() {
        textWorkoutTitle = findViewById(R.id.text_workout_title)
        textDate = findViewById(R.id.text_detail_date)
        btnCalendar = findViewById(R.id.btn_detail_calendar)
        recyclerView = findViewById(R.id.recycler_exercises)
        bottomNavigation = findViewById(R.id.bottom_navigation)

        textWorkoutTitle.text = "$workoutCategory Plan"
    }

    private fun setupRecyclerView() {
        // Fetch the specific list of exercises for this category
        val exercises = WorkoutData.getExercisesByCategory(workoutCategory)
        
        adapter = ExerciseAdapter(exercises) { exercise, isChecked ->
            updateExerciseCompletion(exercise, isChecked)
        }
        
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun setupListeners() {
        textDate.setOnClickListener { showDatePicker() }
        btnCalendar.setOnClickListener { showDatePicker() }

        bottomNavigation.setOnItemSelectedListener { item ->
            if (item.itemId == R.id.navigation_exercise) {
                navigateTo(WorkoutActivity::class.java)
                return@setOnItemSelectedListener true
            }
            val target = when (item.itemId) {
                R.id.navigation_home -> HomeActivity::class.java
                R.id.navigation_diary -> CalorieTrackerActivity::class.java
                R.id.navigation_community -> CommunityActivity::class.java
                else -> null
            }
            target?.let {
                navigateTo(it)
                true
            } ?: false
        }
    }

    private fun navigateTo(activityClass: Class<*>) {
        val intent = Intent(this, activityClass)
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        startActivity(intent)
        overridePendingTransition(0, 0)
    }

    private fun showDatePicker() {
        val datePickerDialog = DatePickerDialog(this, { _, year, month, dayOfMonth ->
            selectedDate.set(year, month, dayOfMonth)
            selectedDateString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate.time)
            updateDateDisplay()
            startRealTimeCompletionListener()
        }, selectedDate.get(Calendar.YEAR), selectedDate.get(Calendar.MONTH), selectedDate.get(Calendar.DAY_OF_MONTH))
        datePickerDialog.show()
    }

    private fun updateDateDisplay() {
        textDate.text = SimpleDateFormat("EEEE, MMM dd", Locale.getDefault()).format(selectedDate.time)
    }

    private fun updateExerciseCompletion(exercise: Exercise, completed: Boolean) {
        val userId = auth.currentUser?.uid ?: return
        // Create a unique ID for this specific exercise completion record
        val docId = "${selectedDateString}_${workoutCategory}_${exercise.id}"

        val ref = firestore.collection("users").document(userId)
            .collection("completed_exercises").document(docId)

        if (completed) {
            val data = hashMapOf(
                "exerciseId" to exercise.id.toString(),
                "workoutCategory" to workoutCategory,
                "date" to selectedDateString,
                "timestamp" to com.google.firebase.Timestamp.now()
            )
            ref.set(data)
        } else {
            ref.delete()
        }
    }

    private fun startRealTimeCompletionListener() {
        val userId = auth.currentUser?.uid ?: return

        completionListener?.remove()
        completionListener = firestore.collection("users").document(userId)
            .collection("completed_exercises")
            .whereEqualTo("date", selectedDateString)
            .whereEqualTo("workoutCategory", workoutCategory)
            .addSnapshotListener { snapshots, _ ->
                val completedIds = snapshots?.map { it.getString("exerciseId") ?: "" } ?: emptyList()
                adapter.setCompletedExercises(completedIds)
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        completionListener?.remove()
    }
}
