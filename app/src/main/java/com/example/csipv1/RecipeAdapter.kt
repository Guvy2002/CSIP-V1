package com.example.csipv1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

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
        private val thumbImage: ImageView = itemView.findViewById(R.id.image_recipe_thumb)

        fun bind(recipe: Recipe) {
            nameText.text = recipe.name
            categoryText.text = recipe.category.uppercase()
            caloriesText.text = "${recipe.calories} kcal"
            proteinText.text = "${recipe.protein}g Protein"
            
            // temporary placeholder images
            if (!recipe.imageUrl.isNullOrEmpty()) {
                Glide.with(itemView.context)
                    .load(recipe.imageUrl)
                    .centerCrop()
                    .placeholder(R.drawable.recipe_placeholder_1)
                    .error(R.drawable.recipe_placeholder_1)
                    .into(thumbImage)
            } else if (recipe.imageResId != 0) {
                thumbImage.setImageResource(recipe.imageResId)
            } else {
                thumbImage.setImageResource(R.drawable.recipe_placeholder_1)
            }
        }
    }
}
