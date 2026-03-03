package com.example.csipv1

import android.content.Intent
import android.os.Bundle
import android.widget.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

/**
 * Optimized Home Activity extending BaseActivity for faster theme/setting application.
 * Now displays real-time progress for calories.
 */
class HomeActivity : BaseActivity() {

    private lateinit var usernameTextView: TextView
    private lateinit var caloriesConsumedText: TextView
    private lateinit var caloriesGoalText: TextView
    
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var mealsListener: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        initializeViews()
        setupListeners()
        loadWelcomeData()
        startRealTimeProgressUpdate()
    }

    private fun initializeViews() {
        usernameTextView = findViewById(R.id.text_username)
        caloriesConsumedText = findViewById(R.id.text_home_calories)
        caloriesGoalText = findViewById(R.id.text_home_goal)
        
        val recipeFlipper: ViewFlipper = findViewById(R.id.recipe_flipper)
        recipeFlipper.flipInterval = 3000
        recipeFlipper.isAutoStart = true

        val tipTextView: TextView = findViewById(R.id.text_tip_of_the_day)
        val healthTips = resources.getStringArray(R.array.health_tips)
        if (healthTips.isNotEmpty()) {
            tipTextView.text = healthTips[Random.nextInt(healthTips.size)]
        }
    }

    private fun setupListeners() {
        findViewById<ImageButton>(R.id.btn_settings).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        findViewById<Button>(R.id.btn_add_meal).setOnClickListener {
            startActivity(Intent(this, CalorieTrackerActivity::class.java))
        }

        findViewById<Button>(R.id.btn_log_workout).setOnClickListener {
            startActivity(Intent(this, WorkoutActivity::class.java))
        }

        findViewById<TextView>(R.id.btn_view_recipes).setOnClickListener {
            startActivity(Intent(this, RecipiesActivity::class.java))
        }

        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigation.selectedItemId = R.id.navigation_home
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> true
                R.id.navigation_diary -> {
                    startActivity(Intent(this, CalorieTrackerActivity::class.java))
                    true
                }
                R.id.navigation_community -> {
                    startActivity(Intent(this, CommunityActivity::class.java))
                    true
                }
                R.id.navigation_exercise -> {
                    startActivity(Intent(this, WorkoutActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun loadWelcomeData() {
        val newUsername = intent.getStringExtra("USER_NAME")
        if (newUsername != null) {
            usernameTextView.text = newUsername
        } else {
            val user = auth.currentUser
            usernameTextView.text = if (user?.displayName.isNullOrEmpty()) "User" else user?.displayName
        }
    }

    private fun startRealTimeProgressUpdate() {
        val userId = auth.currentUser?.uid ?: return
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        // 1. Fetch User Goal
        firestore.collection("users").document(userId)
            .collection("goals").document("current")
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val goal = doc.getLong("dailyCalories") ?: 2000
                    caloriesGoalText.text = goal.toString()
                }
            }

        // 2. Listen for today's calories
        mealsListener?.remove()
        mealsListener = firestore.collection("users").document(userId)
            .collection("meals")
            .whereEqualTo("date", today)
            .addSnapshotListener { snapshots, _ ->
                if (snapshots != null) {
                    val total = snapshots.sumOf { it.getLong("calories") ?: 0 }
                    caloriesConsumedText.text = total.toString()
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        mealsListener?.remove()
    }
}
