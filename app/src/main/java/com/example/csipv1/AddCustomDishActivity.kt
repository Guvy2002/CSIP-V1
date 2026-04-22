package com.example.csipv1

import android.os.Bundle
import android.util.Log
import android.widget.*
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class AddCustomDishActivity : BaseActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private lateinit var editName: TextInputEditText
    private lateinit var editCalories: TextInputEditText
    private lateinit var editServingUnit: TextInputEditText
    private lateinit var editProtein: TextInputEditText
    private lateinit var editCarbs: TextInputEditText
    private lateinit var editFat: TextInputEditText
    private lateinit var spinnerCategory: Spinner
    private lateinit var checkShare: CheckBox
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_custom_dish)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        initializeViews()
        setupSpinner()
        setupListeners()
    }

    private fun initializeViews() {
        editName = findViewById(R.id.edit_dish_name)
        editCalories = findViewById(R.id.edit_calories)
        editServingUnit = findViewById(R.id.edit_serving_unit)
        editProtein = findViewById(R.id.edit_protein)
        editCarbs = findViewById(R.id.edit_carbs)
        editFat = findViewById(R.id.edit_fat)
        spinnerCategory = findViewById(R.id.spinner_category)
        checkShare = findViewById(R.id.check_share_community)
        btnSave = findViewById(R.id.btn_save_dish)
        btnCancel = findViewById(R.id.btn_cancel)
    }

    private fun setupSpinner() {
        val categories = arrayOf("Breakfast", "Lunch", "Dinner", "Snacks")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categories)
        spinnerCategory.adapter = adapter

        val defaultCategory = intent.getStringExtra("DEFAULT_CATEGORY") ?: "Breakfast"
        val position = categories.indexOf(defaultCategory)
        if (position >= 0) spinnerCategory.setSelection(position)
    }

    private fun setupListeners() {
        btnCancel.setOnClickListener { finish() }

        btnSave.setOnClickListener {
            if (validateInput()) {
                saveDishToDiary()
            }
        }
    }

    private fun validateInput(): Boolean {
        if (editName.text.isNullOrBlank()) {
            editName.error = "Name required"
            return false
        }
        if (editCalories.text.isNullOrBlank()) {
            editCalories.error = "Calories required"
            return false
        }
        return true
    }

    private fun saveDishToDiary() {
        val userId = auth.currentUser?.uid ?: return
        val name = editName.text.toString().trim()
        val calories = editCalories.text.toString().toIntOrNull() ?: 0
        val protein = editProtein.text.toString().toIntOrNull() ?: 0
        val carbs = editCarbs.text.toString().toIntOrNull() ?: 0
        val fat = editFat.text.toString().toIntOrNull() ?: 0
        val unit = editServingUnit.text.toString().trim().ifBlank { "serving" }
        val category = spinnerCategory.selectedItem.toString()
        val shouldShare = checkShare.isChecked
        
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        val mealData = hashMapOf(
            "name" to name,
            "calories" to calories,
            "protein" to protein,
            "carbs" to carbs,
            "fat" to fat,
            "baseCalories" to calories,
            "baseProtein" to protein,
            "baseCarbs" to carbs,
            "baseFat" to fat,
            "quantity" to 1.0,
            "unit" to unit,
            "mealType" to category,
            "date" to today
        )

        btnSave.isEnabled = false
        btnSave.text = "Saving..."

        //add to today's log
        firestore.collection("users").document(userId).collection("meals").document()
            .set(mealData)
            .addOnSuccessListener {
                Toast.makeText(this, "$name added to $category", Toast.LENGTH_SHORT).show()
                
                // save to user's personal library
                saveToPersonalLibrary(name, calories, protein, carbs, fat, unit)
                
                //share with community if the user wants too
                if (shouldShare) {
                    shareWithCommunity(name, calories, protein, carbs, fat, unit, category)
                }
                
                finish()
            }
            .addOnFailureListener {
                btnSave.isEnabled = true
                btnSave.text = "Save and Add to Diary"
                Toast.makeText(this, "Failed to save dish", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveToPersonalLibrary(name: String, cal: Int, pro: Int, carb: Int, fat: Int, unit: String) {
        val userId = auth.currentUser?.uid ?: return
        val dishData = hashMapOf(
            "name" to name,
            "calories" to cal,
            "protein" to pro,
            "carbs" to carb,
            "fat" to fat,
            "unit" to unit,
            "createdAt" to System.currentTimeMillis()
        )
        firestore.collection("users").document(userId).collection("custom_dishes")
            .document(name.lowercase())
            .set(dishData)
    }

    private fun shareWithCommunity(dishName: String, cal: Int, pro: Int, carb: Int, fat: Int, unit: String, category: String) {
        val userId = auth.currentUser?.uid ?: return
        
        firestore.collection("users").document(userId).get().addOnSuccessListener { userSnap ->
            val username = userSnap.getString("username") ?: auth.currentUser?.displayName ?: "User"
            
            val feedRef = firestore.collection("activity_feed").document()
            val activity = hashMapOf(
                "id" to feedRef.id,
                "userId" to userId,
                "username" to username,
                "type" to "MEAL",
                "content" to "just created and logged a custom dish: $dishName for $category! 👨‍🍳✨",
                "timestamp" to System.currentTimeMillis(),
                "highFives" to emptyList<String>(),
                "comments" to emptyList<Map<String, Any>>(),
                
                // sharable metadata
                "isSharableDish" to true,
                "dishName" to dishName,
                "calories" to cal,
                "protein" to pro,
                "carbs" to carb,
                "fat" to fat,
                "unit" to unit
            )
            
            firestore.collection("activity_feed").document(feedRef.id).set(activity)
                .addOnFailureListener { e -> Log.e("CustomDish", "Feed post failed", e) }
        }
    }
}
