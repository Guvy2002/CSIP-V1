package com.example.csipv1

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class RecipeDetailActivity : BaseActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_detail)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""
        
        toolbar.setNavigationOnClickListener {
            finish()
        }

        val recipeId = intent.getIntExtra("RECIPE_ID", -1)
        val recipe = RecipeData.indianHealthyRecipes.find { it.id == recipeId }

        recipe?.let { r ->
            findViewById<TextView>(R.id.text_detail_recipe_name).text = r.name
            findViewById<TextView>(R.id.text_detail_category).text = r.category.uppercase()
            findViewById<TextView>(R.id.text_detail_calories).text = r.calories.toString()
            findViewById<TextView>(R.id.text_detail_protein).text = "${r.protein}g"
            findViewById<TextView>(R.id.text_detail_carbs).text = "${r.carbs}g"
            findViewById<TextView>(R.id.text_detail_fat).text = "${r.fat}g"

            val imageDetail = findViewById<ImageView>(R.id.image_recipe_detail)
            
            if (!r.imageUrl.isNullOrEmpty()) {
                Glide.with(this)
                    .load(r.imageUrl)
                    .centerCrop()
                    .placeholder(R.drawable.recipe_placeholder_1)
                    .into(imageDetail)
            } else if (r.imageResId != 0) {
                imageDetail.setImageResource(r.imageResId)
            } else {
                imageDetail.setImageResource(R.drawable.recipe_placeholder_1)
            }

            val ingredientsText = r.ingredients.joinToString("\n") { ingredient -> "• $ingredient" }
            findViewById<TextView>(R.id.text_detail_ingredients).text = ingredientsText

            val instructionsText = r.instructions.mapIndexed { index, instruction -> "${index + 1}. $instruction" }.joinToString("\n")
            findViewById<TextView>(R.id.text_detail_instructions).text = instructionsText

            findViewById<Button>(R.id.btn_add_to_tracker).setOnClickListener {
                showMealTypeSelection(r)
            }
        }
    }

    private fun showMealTypeSelection(recipe: Recipe) {
        val mealTypes = arrayOf("Breakfast", "Lunch", "Dinner", "Snacks")
        AlertDialog.Builder(this)
            .setTitle("Select Meal Type")
            .setItems(mealTypes) { _, which ->
                val selectedMealType = mealTypes[which]
                addRecipeToTracker(recipe, selectedMealType)
            }
            .show()
    }

    private fun addRecipeToTracker(recipe: Recipe, mealType: String) {
        val userId = auth.currentUser?.uid ?: return
        val username = auth.currentUser?.displayName ?: "Someone"
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        
        val mealData = hashMapOf(
            "name" to recipe.name,
            "calories" to recipe.calories,
            "protein" to recipe.protein,
            "carbs" to recipe.carbs,
            "fat" to recipe.fat,
            "baseCalories" to recipe.calories,
            "baseProtein" to recipe.protein,
            "baseCarbs" to recipe.carbs,
            "baseFat" to recipe.fat,
            "quantity" to 1.0,
            "unit" to "serving",
            "mealType" to mealType,
            "date" to today
        )

        val batch = firestore.batch()
        
        val mealRef = firestore.collection("users").document(userId).collection("meals").document()
        batch.set(mealRef, mealData)

        //Update total points
        val userRef = firestore.collection("users").document(userId)
        batch.update(userRef, "points", FieldValue.increment(2))
        
        //points for leaderboard
        val dailyLeaderboardRef = firestore.collection("daily_leaderboard").document("${today}_$userId")
        batch.set(dailyLeaderboardRef, hashMapOf(
            "userId" to userId,
            "username" to username,
            "date" to today,
            "points" to FieldValue.increment(2)
        ), com.google.firebase.firestore.SetOptions.merge())

        //Add to community feed
        val activityId = firestore.collection("activity_feed").document().id
        val activity = hashMapOf(
            "id" to activityId,
            "userId" to userId,
            "username" to username,
            "type" to "MEAL",
            "content" to "just cooked and logged ${recipe.name} for $mealType! 🍳 (+2 pts)",
            "timestamp" to System.currentTimeMillis(),
            "highFives" to emptyList<String>()
        )
        batch.set(firestore.collection("activity_feed").document(activityId), activity)

        batch.commit()
            .addOnSuccessListener {
                Toast.makeText(this, "${recipe.name} added to $mealType! You earned 2 points! 🌟", Toast.LENGTH_LONG).show()
                val intent = Intent(this, CalorieTrackerActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                startActivity(intent)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
