package com.example.csipv1

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.cardview.widget.CardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Optimized Goals Activity extending BaseActivity for faster theme/setting application.
 */
class GoalsActivity : BaseActivity() {

    private lateinit var ageInput: EditText
    private lateinit var genderGroup: RadioGroup
    private lateinit var currentWeightInput: EditText
    private lateinit var heightInput: EditText
    private lateinit var fitnessGoalSpinner: Spinner
    private lateinit var activityLevelSpinner: Spinner
    private lateinit var saveButton: Button

    // Result UI components
    private lateinit var resultsCard: CardView
    private lateinit var textCalcCalories: TextView
    private lateinit var textCalcProtein: TextView
    private lateinit var textCalcCarbs: TextView
    private lateinit var textCalcFat: TextView

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    
    private var isSaved = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goals)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        initializeViews()
        setupSpinners()
        
        // Load existing data if available
        loadExistingGoals()

        val sourceActivity = intent.getStringExtra("SOURCE_ACTIVITY")

        saveButton.setOnClickListener {
            if (isSaved) {
                navigateNext(sourceActivity)
            } else {
                performSave(sourceActivity)
            }
        }
    }

    private fun initializeViews() {
        ageInput = findViewById(R.id.age_input)
        genderGroup = findViewById(R.id.gender_group)
        currentWeightInput = findViewById(R.id.current_weight_input)
        heightInput = findViewById(R.id.height_input)
        fitnessGoalSpinner = findViewById(R.id.fitness_goal_spinner)
        activityLevelSpinner = findViewById(R.id.activity_level_spinner)
        saveButton = findViewById(R.id.save_goals_btn)

        resultsCard = findViewById(R.id.results_card)
        textCalcCalories = findViewById(R.id.text_calc_calories)
        textCalcProtein = findViewById(R.id.text_calc_protein)
        textCalcCarbs = findViewById(R.id.text_calc_carbs)
        textCalcFat = findViewById(R.id.text_calc_fat)
    }

    private fun setupSpinners() {
        ArrayAdapter.createFromResource(
            this,
            R.array.activity_levels,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            activityLevelSpinner.adapter = adapter
        }

        ArrayAdapter.createFromResource(
            this,
            R.array.fitness_goals,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            fitnessGoalSpinner.adapter = adapter
        }
    }

    private fun loadExistingGoals() {
        val userId = auth.currentUser?.uid ?: return
        
        firestore.collection("users").document(userId)
            .collection("goals").document("current")
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    ageInput.setText(document.getLong("age")?.toString() ?: "")
                    currentWeightInput.setText(document.getDouble("weight")?.toString() ?: "")
                    heightInput.setText(document.getDouble("height")?.toString() ?: "")
                    
                    val gender = document.getString("gender") ?: "Male"
                    if (gender == "Male") {
                        genderGroup.check(R.id.gender_male)
                    } else {
                        genderGroup.check(R.id.gender_female)
                    }
                    
                    val fitnessGoal = document.getString("fitnessGoal") ?: ""
                    val activityLevel = document.getString("activityLevel") ?: ""
                    
                    setSpinnerValue(fitnessGoalSpinner, fitnessGoal)
                    setSpinnerValue(activityLevelSpinner, activityLevel)
                    
                    val calories = document.getLong("dailyCalories")?.toInt() ?: 0
                    if (calories > 0) {
                        val plan = CalorieCalculator.NutritionPlan(
                            calories,
                            document.getLong("proteinTarget")?.toInt() ?: 0,
                            document.getLong("carbsTarget")?.toInt() ?: 0,
                            document.getLong("fatTarget")?.toInt() ?: 0
                        )
                        showResults(plan)
                    }
                }
            }
    }

    private fun setSpinnerValue(spinner: Spinner, value: String) {
        val adapter = spinner.adapter as? ArrayAdapter<String> ?: return
        val position = adapter.getPosition(value)
        if (position >= 0) {
            spinner.setSelection(position)
        }
    }

    private fun performSave(sourceActivity: String?) {
        val ageStr = ageInput.text.toString().trim()
        val weightStr = currentWeightInput.text.toString().trim()
        val heightStr = heightInput.text.toString().trim()

        if (ageStr.isEmpty() || weightStr.isEmpty() || heightStr.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
            return
        }

        val age = ageStr.toInt()
        val weight = weightStr.toDouble()
        val height = heightStr.toDouble()
        val gender = if (genderGroup.checkedRadioButtonId == R.id.gender_male) "Male" else "Female"
        val fitnessGoal = fitnessGoalSpinner.selectedItem.toString()
        val activityLevel = activityLevelSpinner.selectedItem.toString()

        val plan = CalorieCalculator.calculateNutrition(
            age, gender, weight, height, activityLevel, fitnessGoal
        )

        showResults(plan)
        saveToFirebase(plan, age, weight, height, gender, fitnessGoal, activityLevel)
    }

    private fun showResults(plan: CalorieCalculator.NutritionPlan) {
        resultsCard.visibility = View.VISIBLE
        textCalcCalories.text = getString(R.string.calories_format, plan.calories)
        textCalcProtein.text = getString(R.string.protein_format, plan.protein)
        textCalcCarbs.text = getString(R.string.carbs_format, plan.carbs)
        textCalcFat.text = getString(R.string.fat_format, plan.fat)
        
        saveButton.text = "Continue"
        isSaved = true
    }

    private fun saveToFirebase(
        plan: CalorieCalculator.NutritionPlan,
        age: Int,
        weight: Double,
        height: Double,
        gender: String,
        fitnessGoal: String,
        activityLevel: String
    ) {
        val userId = auth.currentUser?.uid ?: return

        val goalData = hashMapOf(
            "age" to age,
            "weight" to weight,
            "height" to height,
            "gender" to gender,
            "fitnessGoal" to fitnessGoal,
            "activityLevel" to activityLevel,
            "dailyCalories" to plan.calories,
            "proteinTarget" to plan.protein,
            "carbsTarget" to plan.carbs,
            "fatTarget" to plan.fat,
            "timestamp" to com.google.firebase.Timestamp.now()
        )

        firestore.collection("users").document(userId)
            .collection("goals").document("current")
            .set(goalData)
            .addOnSuccessListener {
                Toast.makeText(this, "Goals saved successfully!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error saving to cloud: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun navigateNext(sourceActivity: String?) {
        if (sourceActivity == "SETTINGS") {
            val intent = Intent(this, SettingsActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        } else {
            val username = intent.getStringExtra("USER_NAME")
            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra("USER_NAME", username)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        finish()
    }
}
