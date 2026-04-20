package com.example.csipv1

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.card.MaterialCardView
import kotlin.random.Random

class RecipeListActivity : BaseActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var dietCategoriesLayout: LinearLayout
    private lateinit var titleTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_list)

        titleTextView = findViewById(R.id.text_recipe_list_title)
        recyclerView = findViewById(R.id.recycler_recipes)
        dietCategoriesLayout = findViewById(R.id.layout_diet_categories)

        val category = intent.getStringExtra("CATEGORY") ?: "All"
        val dietType = intent.getStringExtra("DIET_TYPE")

        findViewById<ImageButton>(R.id.btn_back).setOnClickListener {
            if (recyclerView.visibility == View.VISIBLE && dietType == null && category == "All") {
                showDietCategories()
            } else {
                onBackPressed()
            }
        }

        setupDietCategoryButtons()
        
        if (dietType != null) {
            showRecipeList(null, dietType)
        } else if (category != "All") {
            showRecipeList(category, null)
        } else {
            showDietCategories()
        }

        setupBottomNavigation()
        displayRandomCookingTip()
    }

    private fun setupDietCategoryButtons() {
        findViewById<MaterialCardView>(R.id.card_veg).setOnClickListener {
            showRecipeList(null, "Veg")
        }
        findViewById<MaterialCardView>(R.id.card_non_veg).setOnClickListener {
            showRecipeList(null, "Non-Veg")
        }
        findViewById<MaterialCardView>(R.id.card_vegan).setOnClickListener {
            showRecipeList(null, "Vegan")
        }
    }

    private fun showDietCategories() {
        titleTextView.text = "Healthy Recipes"
        dietCategoriesLayout.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
    }

    private fun showRecipeList(category: String?, dietType: String?) {
        dietCategoriesLayout.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE

        val displayTitle = when {
            dietType != null -> "$dietType Recipes"
            category != null -> "$category Recipes"
            else -> "Healthy Recipes"
        }
        titleTextView.text = displayTitle

        val filteredRecipes = when {
            dietType != null -> RecipeData.indianHealthyRecipes.filter { it.dietType.equals(dietType, ignoreCase = true) }
            category != null && category != "All" -> RecipeData.indianHealthyRecipes.filter { it.category.equals(category, ignoreCase = true) }
            else -> RecipeData.indianHealthyRecipes
        }

        val adapter = RecipeAdapter(filteredRecipes) { recipe ->
            val intent = Intent(this, RecipeDetailActivity::class.java)
            intent.putExtra("RECIPE_ID", recipe.id)
            startActivity(intent)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun displayRandomCookingTip() {
        val tipTextView: TextView = findViewById(R.id.text_cooking_tip)
        val cookingTips = resources.getStringArray(R.array.cooking_tips)
        if (cookingTips.isNotEmpty()) {
            tipTextView.text = cookingTips[Random.nextInt(cookingTips.size)]
        }
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
