package com.example.csipv1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * Redesigned Recipes Activity with professional unified styling and instant-response navigation.
 */
class RecipiesActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipies)

        setupCategoryButtons()
        setupBottomNavigation()
    }

    private fun setupCategoryButtons() {
        findViewById<Button>(R.id.btn_breakfast_recipes).setOnClickListener {
            openRecipeList("Breakfast")
        }
        findViewById<Button>(R.id.btn_lunch_recipes).setOnClickListener {
            openRecipeList("Lunch")
        }
        findViewById<Button>(R.id.btn_dinner_recipes).setOnClickListener {
            openRecipeList("Dinner")
        }
        findViewById<Button>(R.id.btn_snack_recipes).setOnClickListener {
            openRecipeList("Snacks")
        }
    }

    private fun openRecipeList(category: String) {
        val intent = Intent(this, RecipeListActivity::class.java)
        intent.putExtra("CATEGORY", category)
        startActivity(intent)
    }

    private fun setupBottomNavigation() {
        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
        
        bottomNavigation.setOnItemSelectedListener { item ->
            val target = when (item.itemId) {
                R.id.navigation_home -> HomeActivity::class.java
                R.id.navigation_diary -> CalorieTrackerActivity::class.java
                R.id.navigation_community -> CommunityActivity::class.java
                R.id.navigation_exercise -> WorkoutActivity::class.java
                else -> null
            }

            target?.let {
                val intent = Intent(this, it)
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                startActivity(intent)
                overridePendingTransition(0, 0)
                true
            } ?: false
        }
    }
}
