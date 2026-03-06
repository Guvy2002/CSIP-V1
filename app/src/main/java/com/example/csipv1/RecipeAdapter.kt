package com.example.csipv1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecipeAdapter(
    private val recipes: List<Recipe>,
    private val onRecipeClick: (Recipe) -> Unit
) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recipe, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipes[position]
        holder.bind(recipe)
        holder.itemView.setOnClickListener { onRecipeClick(recipe) }
    }

    override fun getItemCount() = recipes.size

    class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameText: TextView = itemView.findViewById(R.id.text_recipe_name)
        private val categoryText: TextView = itemView.findViewById(R.id.text_recipe_category)
        private val caloriesText: TextView = itemView.findViewById(R.id.text_recipe_calories)
        private val proteinText: TextView = itemView.findViewById(R.id.text_recipe_protein)

        fun bind(recipe: Recipe) {
            nameText.text = recipe.name
            categoryText.text = recipe.category.uppercase()
            caloriesText.text = "${recipe.calories} kcal"
            proteinText.text = "P: ${recipe.protein}g"
        }
    }
}
