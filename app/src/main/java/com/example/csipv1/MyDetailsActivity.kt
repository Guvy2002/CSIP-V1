package com.example.csipv1

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class MyDetailsActivity : BaseActivity() {

    private lateinit var textWeight: TextView
    private lateinit var textHeight: TextView
    private lateinit var textAge: TextView
    private lateinit var textGender: TextView
    private lateinit var textCalories: TextView
    private lateinit var textMacros: TextView
    private lateinit var textUsername: TextView

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_details)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        initializeViews()
        loadUserProfile()
        setupListeners()
    }

    private fun initializeViews() {
        textWeight = findViewById(R.id.stat_weight)
        textHeight = findViewById(R.id.stat_height)
        textAge = findViewById(R.id.stat_age)
        textGender = findViewById(R.id.stat_gender)
        textCalories = findViewById(R.id.target_calories)
        textMacros = findViewById(R.id.target_macros)
        textUsername = findViewById(R.id.profile_username)
        
        val user = auth.currentUser
        textUsername.text = user?.displayName ?: "My Profile"
    }

    private fun loadUserProfile() {
        val userId = auth.currentUser?.uid ?: return

        firestore.collection("users").document(userId)
            .collection("goals").document("current")
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val weight = doc.getDouble("weight") ?: 0.0
                    val height = doc.getDouble("height") ?: 0.0
                    val age = doc.getLong("age") ?: 0
                    val gender = doc.getString("gender") ?: "Not Set"
                    
                    val calories = doc.getLong("dailyCalories") ?: 0
                    val protein = doc.getLong("proteinTarget") ?: 0
                    val carbs = doc.getLong("carbsTarget") ?: 0
                    val fat = doc.getLong("fatTarget") ?: 0

                    textWeight.text = "Weight: $weight kg"
                    textHeight.text = "Height: $height cm"
                    textAge.text = "Age: $age"
                    textGender.text = "Gender: $gender"
                    
                    textCalories.text = "Daily Calories: $calories kcal"
                    textMacros.text = "Macros: P ${protein}g | C ${carbs}g | F ${fat}g"
                }
            }
    }

    private fun setupListeners() {
        findViewById<MaterialButton>(R.id.btn_update_goals).setOnClickListener {
            val intent = Intent(this, GoalsActivity::class.java)
            intent.putExtra("SOURCE_ACTIVITY", "SETTINGS")
            startActivity(intent)
        }

        findViewById<MaterialButton>(R.id.btn_back_to_settings).setOnClickListener {
            finish()
        }
    }
}
