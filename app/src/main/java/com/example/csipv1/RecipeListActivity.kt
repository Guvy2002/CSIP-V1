package com.example.csipv1

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class RecipeListActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_list)

        val category = intent.getStringExtra("CATEGORY") ?: "All"
        val titleTextView = findViewById<TextView>(R.id.text_recipe_list_title)
        if (titleTextView != null) {
            titleTextView.text = if (category == "All") "Healthy Recipes" else "$category Recipes"
        }

        findViewById<ImageButton>(R.id.btn_back).setOnClickListener {
            onBackPressed()
        }

        setupRecyclerView(category)
        setupBottomNavigation()
    }

    private fun setupRecyclerView(category: String) {
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_recipes)
        
        val filteredRecipes = if (category == "All") {
            RecipeData.indianHealthyRecipes
        } else {
            RecipeData.indianHealthyRecipes.filter { it.category.equals(category, ignoreCase = true) }
        }

        val adapter = RecipeAdapter(filteredRecipes) { recipe ->
            val intent = Intent(this, RecipeDetailActivity::class.java)
            intent.putExtra("RECIPE_ID", recipe.id)
            startActivity(intent)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
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
