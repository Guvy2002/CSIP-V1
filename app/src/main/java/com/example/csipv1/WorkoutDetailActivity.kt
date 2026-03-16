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
import java.util.Calendar
import java.util.Date
import java.util.Locale

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

        try {
            setContentView(R.layout.activity_workoutdetails)

            auth = FirebaseAuth.getInstance()
            firestore = FirebaseFirestore.getInstance()

            workoutCategory = intent.getStringExtra("WORKOUT_CATEGORY") ?: "Chest"
            selectedDateString = intent.getStringExtra("SELECTED_DATE")
                ?: SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

            textWorkoutTitle = findViewById(R.id.text_workout_title)
            textDate = findViewById(R.id.text_detail_date)
            btnCalendar = findViewById(R.id.btn_detail_calendar)
            recyclerView = findViewById(R.id.recycler_exercises)
            bottomNavigation = findViewById(R.id.bottom_navigation)

            try {
                selectedDate.time = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(selectedDateString) ?: Date()
            } catch (_: Exception) {
                selectedDate = Calendar.getInstance()
            }

            textWorkoutTitle.text = "$workoutCategory Workouts"
            textDate.text = SimpleDateFormat("EEEE, MMM dd", Locale.getDefault()).format(selectedDate.time)

            btnCalendar.setOnClickListener { showDatePicker() }
            textDate.setOnClickListener { showDatePicker() }

            setupRecyclerView()
            setupBottomNavigation()
            startRealTimeCompletionListener()

        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupRecyclerView() {
        val exercises = WorkoutData.getExercisesByCategory(workoutCategory)

        adapter = ExerciseAdapter(
            exercises,
            { exercise, isChecked ->
                updateExerciseCompletion(exercise, isChecked)
            },
            { exercise ->
                val intent = Intent(this, ExerciseDetailActivity::class.java)
                intent.putExtra("EXERCISE_ID", exercise.id)
                startActivity(intent)
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun updateExerciseCompletion(exercise: Exercise, completed: Boolean) {
        val userId = auth.currentUser?.uid ?: return
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

    private fun setupBottomNavigation() {
        bottomNavigation.selectedItemId = R.id.navigation_exercise

        bottomNavigation.setOnItemSelectedListener { item ->
            if (item.itemId == R.id.navigation_exercise) {
                return@setOnItemSelectedListener true
            }

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
                overridePendingTransition(0, 0)
                true
            } ?: false
        }
    }

    private fun showDatePicker() {
        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                selectedDate.set(year, month, dayOfMonth)
                selectedDateString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate.time)
                textDate.text = SimpleDateFormat("EEEE, MMM dd", Locale.getDefault()).format(selectedDate.time)
                startRealTimeCompletionListener()
            },
            selectedDate.get(Calendar.YEAR),
            selectedDate.get(Calendar.MONTH),
            selectedDate.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        completionListener?.remove()
    }
}