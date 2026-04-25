package com.example.csipv1

import android.app.*
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import java.text.SimpleDateFormat
import java.util.*

class StepCounterService : Service(), SensorEventListener {

    private var sensorManager: SensorManager? = null
    private var stepSensor: Sensor? = null
    private var previousTotalSteps = 0f
    
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        
        if (stepSensor != null) {
            sensorManager?.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
        }
        
        val sharedPrefs = getSharedPreferences("StepPrefs", Context.MODE_PRIVATE)
        previousTotalSteps = sharedPrefs.getFloat("previousTotalSteps", 0f)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = createNotification("Tracking your steps...")
        startForeground(1, notification)
        return START_STICKY
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
            val totalStepsSinceBoot = event.values[0]
            val todaySteps = (totalStepsSinceBoot - previousTotalSteps).toInt()
            
            if (todaySteps >= 0) {
                updateStepsAndCheckPoints(todaySteps)
                updateNotification("You have taken $todaySteps steps today!")
            } else {
                previousTotalSteps = totalStepsSinceBoot
                saveOffset(totalStepsSinceBoot)
            }
        }
    }

    private fun updateStepsAndCheckPoints(steps: Int) {
        val userId = auth.currentUser?.uid ?: return
        val userRef = db.collection("users").document(userId)
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val dailyLeaderboardRef = db.collection("daily_leaderboard").document("${today}_$userId")
        
        userRef.get().addOnSuccessListener { doc ->
            if (!doc.exists()) return@addOnSuccessListener

            val lastPointsSteps = doc.getLong("lastPointsSteps") ?: 0
            var pointsToAdd = 0L
            var milestoneReached = ""
            
            if (steps >= 5000 && lastPointsSteps < 5000) {
                pointsToAdd += 5
                milestoneReached = "5,000 steps"
            }
            if (steps >= 10000 && lastPointsSteps < 10000) {
                pointsToAdd += 5
                milestoneReached = "10,000 steps"
            }

            val updates = mutableMapOf<String, Any>(
                "dailySteps" to steps
            )
            
            if (pointsToAdd > 0) {
                updates["points"] = FieldValue.increment(pointsToAdd)
                updates["lastPointsSteps"] = steps.toLong()


                db.runTransaction { transaction ->
                    transaction.set(dailyLeaderboardRef, hashMapOf(
                        "userId" to userId,
                        "username" to (doc.getString("username") ?: "User"),
                        "date" to today,
                        "points" to FieldValue.increment(pointsToAdd)
                    ), com.google.firebase.firestore.SetOptions.merge())
                }

                postMilestoneToFeed(userId, doc.getString("username") ?: "User", milestoneReached)
            }
            
            userRef.update(updates)
        }.addOnFailureListener { e ->
            Log.e("StepService", "Error updating steps", e)
        }
    }

    private fun postMilestoneToFeed(userId: String, username: String, milestone: String) {
        val feedRef = db.collection("activity_feed").document()
        val activity = hashMapOf(
            "id" to feedRef.id,
            "userId" to userId,
            "username" to username,
            "type" to "WORKOUT",
            "content" to "just reached their $milestone goal! 🏃‍♂️💨",
            "timestamp" to System.currentTimeMillis(),
            "highFives" to emptyList<String>(),
            "comments" to emptyList<Map<String, Any>>()
        )
        db.collection("activity_feed").document(feedRef.id).set(activity)
            .addOnFailureListener { e -> Log.e("StepService", "Feed post failed", e) }
    }

    private fun createNotification(content: String): Notification {
        val notificationIntent = Intent(this, HomeActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, "step_channel")
            .setContentTitle("Step Tracker Active")
            .setContentText(content)
            .setSmallIcon(R.drawable.fitness_24)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    private fun updateNotification(content: String) {
        val notification = createNotification(content)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                "step_channel", "Step Tracker Channel",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    private fun saveOffset(offset: Float) {
        val sharedPrefs = getSharedPreferences("StepPrefs", Context.MODE_PRIVATE)
        sharedPrefs.edit().putFloat("previousTotalSteps", offset).apply()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        sensorManager?.unregisterListener(this)
    }
}
