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
 * Optimized Home Activity with Instant-Response navigation and real-time data sync.
 */
class HomeActivity : BaseActivity() {

    private lateinit var usernameTextView: TextView
    private lateinit var caloriesConsumedText: TextView
    private lateinit var caloriesGoalText: TextView
    private lateinit var bottomNavigation: BottomNavigationView
    
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

    override fun onResume() {
        super.onResume()
        // Ensure the correct icon is highlighted when returning to this page
        bottomNavigation.selectedItemId = R.id.navigation_home
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        // Refresh data if needed when activity is brought to front
        loadWelcomeData()
    }

    private fun initializeViews() {
        usernameTextView = findViewById(R.id.text_username)
        caloriesConsumedText = findViewById(R.id.text_home_calories)
        caloriesGoalText = findViewById(R.id.text_home_goal)
        bottomNavigation = findViewById(R.id.bottom_navigation)
        
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
            navigateTo(SettingsActivity::class.java)
        }

        findViewById<Button>(R.id.btn_add_meal).setOnClickListener {
            navigateTo(CalorieTrackerActivity::class.java)
        }

        findViewById<Button>(R.id.btn_log_workout).setOnClickListener {
            navigateTo(WorkoutActivity::class.java)
        }

        findViewById<TextView>(R.id.btn_view_recipes).setOnClickListener {
            navigateTo(RecipiesActivity::class.java)
        }

        // --- Instant-Response Bottom Navigation ---
        bottomNavigation.setOnItemSelectedListener { item ->
            if (item.itemId == R.id.navigation_home) return@setOnItemSelectedListener true

            val target = when (item.itemId) {
                R.id.navigation_diary -> CalorieTrackerActivity::class.java
                R.id.navigation_community -> CommunityActivity::class.java
                R.id.navigation_exercise -> WorkoutActivity::class.java
                else -> null
            }

            target?.let {
                navigateTo(it)
                true
            } ?: false
        }
    }

    private fun navigateTo(activityClass: Class<*>) {
        if (this::class.java == activityClass) return
        val intent = Intent(this, activityClass)
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        startActivity(intent)
        overridePendingTransition(0, 0)
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

        firestore.collection("users").document(userId)
            .collection("goals").document("current")
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val goal = doc.getLong("dailyCalories") ?: 2000
                    caloriesGoalText.text = goal.toString()
                }
            }

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
