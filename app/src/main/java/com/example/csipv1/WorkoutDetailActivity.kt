package com.example.csipv1

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
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
    
    private lateinit var cardWarmup: MaterialCardView
    private lateinit var textWarmupContent: TextView
    private lateinit var cardCooldown: MaterialCardView
    private lateinit var textCooldownContent: TextView

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
            
            cardWarmup = findViewById(R.id.card_warmup)
            textWarmupContent = findViewById(R.id.text_warmup_content)
            cardCooldown = findViewById(R.id.card_cooldown)
            textCooldownContent = findViewById(R.id.text_cooldown_content)

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
            setupWarmupAndCooldown()
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

    private fun setupWarmupAndCooldown() {
        val workout = WorkoutData.getWorkoutByCategory(workoutCategory)
        
        if (workout != null) {
            if (workout.warmUp.isNotEmpty()) {
                cardWarmup.visibility = View.VISIBLE
                textWarmupContent.text = workout.warmUp.joinToString("\n") { "• $it" }
            } else {
                cardWarmup.visibility = View.GONE
            }

            if (workout.coolDown.isNotEmpty()) {
                cardCooldown.visibility = View.VISIBLE
                textCooldownContent.text = workout.coolDown.joinToString("\n") { "• $it" }
            } else {
                cardCooldown.visibility = View.GONE
            }
        } else {
            cardWarmup.visibility = View.GONE
            cardCooldown.visibility = View.GONE
        }
    }

    private fun updateExerciseCompletion(exercise: Exercise, completed: Boolean) {
        val userId = auth.currentUser?.uid ?: return
        val username = auth.currentUser?.displayName ?: "Someone"
        val docId = "${selectedDateString}_${workoutCategory}_${exercise.id}"
        
        // We use a specific ID for the feed item so we can find and delete it later if unticked
        val feedId = "feed_${userId}_${docId}"

        val userRef = firestore.collection("users").document(userId)
        val exerciseRef = userRef.collection("completed_exercises").document(docId)
        val feedRef = firestore.collection("activity_feed").document(feedId)

        firestore.runTransaction { transaction ->
            val userSnap = transaction.get(userRef)
            val currentPoints = userSnap.getLong("points") ?: 0L
            val exerciseExists = transaction.get(exerciseRef).exists()

            if (completed && !exerciseExists) {
                // 1. Save completion
                val data = hashMapOf(
                    "exerciseId" to exercise.id.toString(),
                    "workoutCategory" to workoutCategory,
                    "date" to selectedDateString,
                    "timestamp" to com.google.firebase.Timestamp.now()
                )
                transaction.set(exerciseRef, data)
                
                // 2. Award points
                transaction.update(userRef, "points", FieldValue.increment(2))
                
                // 3. Post to Community Feed with specific ID
                val activity = hashMapOf(
                    "id" to feedId,
                    "userId" to userId,
                    "username" to username,
                    "type" to "WORKOUT",
                    "content" to "just completed ${exercise.name}! 💪",
                    "timestamp" to System.currentTimeMillis(),
                    "highFives" to emptyList<String>()
                )
                transaction.set(feedRef, activity)

            } else if (!completed && exerciseExists) {
                // 1. Remove completion record
                transaction.delete(exerciseRef)
                
                // 2. Deduct points safely (never below 0)
                val pointsToDeduct = if (currentPoints >= 2) -2L else -currentPoints
                transaction.update(userRef, "points", FieldValue.increment(pointsToDeduct))
                
                // 3. REMOVE from Community Feed so the "Pulse" stays accurate
                transaction.delete(feedRef)
            }
            
            if (currentPoints < 0) {
                transaction.update(userRef, "points", 0)
            }
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Sync error: ${e.message}", Toast.LENGTH_SHORT).show()
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
