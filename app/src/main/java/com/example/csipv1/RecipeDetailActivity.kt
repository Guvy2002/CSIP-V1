package com.example.csipv1

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar

class RecipeDetailActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_detail)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "" // Title is handled by content layout
        
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        val recipeId = intent.getIntExtra("RECIPE_ID", -1)
        val recipe = RecipeData.indianHealthyRecipes.find { it.id == recipeId }

        recipe?.let {
            findViewById<TextView>(R.id.text_detail_recipe_name).text = it.name
            findViewById<TextView>(R.id.text_detail_category).text = it.category.uppercase()
            findViewById<TextView>(R.id.text_detail_calories).text = it.calories.toString()
            findViewById<TextView>(R.id.text_detail_protein).text = "${it.protein}g"
            findViewById<TextView>(R.id.text_detail_carbs).text = "${it.carbs}g"
            findViewById<TextView>(R.id.text_detail_fat).text = "${it.fat}g"

            val imageDetail = findViewById<ImageView>(R.id.image_recipe_detail)
            if (it.imageResId != 0) {
                imageDetail.setImageResource(it.imageResId)
            } else {
                imageDetail.setImageResource(R.drawable.recipe_placeholder_1)
            }

            val ingredientsText = it.ingredients.joinToString("\n") { ingredient -> "• $ingredient" }
            findViewById<TextView>(R.id.text_detail_ingredients).text = ingredientsText

            val instructionsText = it.instructions.mapIndexed { index, instruction -> "${index + 1}. $instruction" }.joinToString("\n")
            findViewById<TextView>(R.id.text_detail_instructions).text = instructionsText
        }
    }
}
