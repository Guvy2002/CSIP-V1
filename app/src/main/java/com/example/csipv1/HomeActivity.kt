package com.example.csipv1

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.widget.*
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

class HomeActivity : BaseActivity(), SensorEventListener {

    private lateinit var usernameTextView: TextView
    private lateinit var caloriesConsumedText: TextView
    private lateinit var caloriesGoalText: TextView
    private lateinit var bottomNavigation: BottomNavigationView
    
    private lateinit var stepsTextView: TextView
    private lateinit var stepsProgressBar: CircularProgressIndicator
    private lateinit var healthTipTextView: TextView
    
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var mealsListener: ListenerRegistration? = null
    private var userListener: ListenerRegistration? = null
    private var goalsListener: ListenerRegistration? = null

    private var sensorManager: SensorManager? = null
    private var running = false
    private var totalSteps = 0f
    private var previousTotalSteps = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        initializeViews()
        setupListeners()
        loadWelcomeData()
        startRealTimeProgressUpdate()
        displayRandomHealthTip()
        
        checkPermissionsAndStartTracking()
    }

    private fun checkPermissionsAndStartTracking() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACTIVITY_RECOGNITION) 
                != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.ACTIVITY_RECOGNITION), 100)
            } else {
                startStepService()
                setupLocalStepSensor()
            }
        } else {
            startStepService()
            setupLocalStepSensor()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startStepService()
            setupLocalStepSensor()
        }
    }

    private fun startStepService() {
        val serviceIntent = Intent(this, StepCounterService::class.java)
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent)
            } else {
                startService(serviceIntent)
            }
        } catch (e: Exception) {
            // Log error or show toast if service fails to start
        }
    }

    private fun setupLocalStepSensor() {
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        loadStepData()
    }

    private fun initializeViews() {
        usernameTextView = findViewById(R.id.text_username)
        caloriesConsumedText = findViewById(R.id.text_home_calories)
        caloriesGoalText = findViewById(R.id.text_home_goal)
        bottomNavigation = findViewById(R.id.bottom_navigation)
        stepsTextView = findViewById(R.id.text_home_steps)
        stepsProgressBar = findViewById(R.id.progress_home_steps)
        healthTipTextView = findViewById(R.id.text_health_tip)
    }

    private fun displayRandomHealthTip() {
        val healthTips = resources.getStringArray(R.array.health_tips)
        if (healthTips.isNotEmpty()) {
            healthTipTextView.text = healthTips[Random.nextInt(healthTips.size)]
        }
    }

    override fun onResume() {
        super.onResume()
        running = true
        bottomNavigation.selectedItemId = R.id.navigation_home
        
        val stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        if (stepSensor != null) {
            sensorManager?.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
        }
        
        // Refresh Auth data just in case
        auth.currentUser?.reload()?.addOnSuccessListener {
            loadWelcomeData()
        }
    }

    override fun onPause() {
        super.onPause()
        running = false
    }

    private fun loadStepData() {
        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        previousTotalSteps = sharedPreferences.getFloat("key1", 0f)
    }

    private fun saveStepData() {
        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putFloat("key1", previousTotalSteps)
        editor.apply()
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (running && event != null) {
            totalSteps = event.values[0]
            val currentSteps = totalSteps.toInt() - previousTotalSteps.toInt()
            if (currentSteps >= 0) {
                stepsTextView.text = currentSteps.toString()
                val progress = (currentSteps * 100 / 10000).coerceIn(0, 100)
                stepsProgressBar.setProgress(progress, true)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun setupListeners() {
        findViewById<ImageButton>(R.id.btn_settings).setOnClickListener { navigateTo(SettingsActivity::class.java) }
        findViewById<Button>(R.id.btn_add_meal).setOnClickListener { navigateTo(CalorieTrackerActivity::class.java) }
        findViewById<Button>(R.id.btn_log_workout).setOnClickListener { navigateTo(WorkoutActivity::class.java) }
        findViewById<TextView>(R.id.btn_view_recipes).setOnClickListener { navigateTo(RecipeListActivity::class.java) }

        bottomNavigation.setOnItemSelectedListener { item ->
            if (item.itemId == R.id.navigation_home) return@setOnItemSelectedListener true
            val target = when (item.itemId) {
                R.id.navigation_diary -> CalorieTrackerActivity::class.java
                R.id.navigation_community -> CommunityActivity::class.java
                R.id.navigation_exercise -> WorkoutActivity::class.java
                else -> null
            }
            target?.let { navigateTo(it); true } ?: false
        }
        
        stepsTextView.setOnClickListener {
            previousTotalSteps = totalSteps
            stepsTextView.text = "0"
            saveStepData()
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
        val user = auth.currentUser
        usernameTextView.text = if (user?.displayName.isNullOrEmpty()) "User" else user?.displayName
    }

    private fun startRealTimeProgressUpdate() {
        val userId = auth.currentUser?.uid ?: return
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        userListener?.remove()
        userListener = firestore.collection("users").document(userId)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null && snapshot.exists()) {
                    // Update username in real-time if it exists in Firestore
                    val firestoreUsername = snapshot.getString("username")
                    if (!firestoreUsername.isNullOrEmpty()) {
                        usernameTextView.text = firestoreUsername
                    }

                    val steps = snapshot.getLong("dailySteps") ?: 0
                    if (!running) {
                        stepsTextView.text = steps.toString()
                        val progress = (steps.toInt() * 100 / 10000).coerceIn(0, 100)
                        stepsProgressBar.setProgress(progress, true)
                    }
                }
            }

        goalsListener?.remove()
        goalsListener = firestore.collection("users").document(userId)
            .collection("goals").document("current")
            .addSnapshotListener { doc, _ ->
                if (doc != null && doc.exists()) {
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
        userListener?.remove()
        goalsListener?.remove()
    }
}
