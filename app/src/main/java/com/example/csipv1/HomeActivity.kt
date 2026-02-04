package com.example.csipv1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.ViewFlipper
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlin.random.Random

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // --- Find All Views ---
        val settingsButton: ImageButton = findViewById(R.id.btn_settings)
        val addMealButton: Button = findViewById(R.id.btn_add_meal)
        val viewWorkoutsButton: Button = findViewById(R.id.btn_log_workout)
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        val tipTextView: TextView = findViewById(R.id.text_tip_of_the_day)
        val recipeFlipper: ViewFlipper = findViewById(R.id.recipe_flipper)
        val viewRecipesButton: Button = findViewById(R.id.btn_view_recipes)
        val usernameTextView: TextView = findViewById(R.id.text_username)

        // --- Get Username and Welcome User ---
        val newUsername = intent.getStringExtra("USER_NAME")
        val isAdmin = intent.getBooleanExtra("IS_ADMIN", false)

        when {
            newUsername != null -> {
                // Case 1: Just registered, username is in the intent
                usernameTextView.text = newUsername
            }
            isAdmin -> {
                // Case 2: Logged in as admin
                usernameTextView.text = "Admin"
            }
            else -> {
                // Case 3: Regular login, get from Firebase Auth
                val user = FirebaseAuth.getInstance().currentUser
                val username = user?.displayName
                if (username != null && username.isNotEmpty()) {
                    usernameTextView.text = username
                } else {
                    // Fallback if the display name is not set
                    usernameTextView.text = "User"
                }
            }
        }

        // --- Setup Recipe Slideshow ---
        recipeFlipper.flipInterval = 3000 // 3 seconds
        recipeFlipper.isAutoStart = true

        // --- Set Random Health Tip ---
        val healthTips = resources.getStringArray(R.array.health_tips)
        val randomTip = healthTips[Random.nextInt(healthTips.size)]
        tipTextView.text = randomTip

        // --- Set Click Listeners ---

        // Top-right Settings Button
        settingsButton.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        // "Add Meal" Button
        addMealButton.setOnClickListener {
            val intent = Intent(this, FoodDiaryActivity::class.java)
            startActivity(intent)
        }

        // "View Workouts" Button
        viewWorkoutsButton.setOnClickListener {
            val intent = Intent(this, WorkoutActivity::class.java)
            startActivity(intent)
        }

        // "View All Recipes" Button
        viewRecipesButton.setOnClickListener {
            val intent = Intent(this, RecipiesActivity::class.java)
            startActivity(intent)
        }

        // --- Bottom Navigation ---
        bottomNavigationView.selectedItemId = R.id.navigation_home
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> true // Do nothing, already here
                R.id.navigation_diary -> {
                    startActivity(Intent(this, FoodDiaryActivity::class.java))
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
}
