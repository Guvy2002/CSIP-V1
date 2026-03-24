package com.example.csipv1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FoodAdapter(
    private var items: List<Food>,
    private val onItemClicked: (Food) -> Unit
) : RecyclerView.Adapter<FoodAdapter.FoodViewHolder>() {

    // Store actual food objects so they persist across different search queries
    private val selectedFoods = mutableMapOf<String, Food>()

    fun getSelectedFoods(): List<Food> = selectedFoods.values.toList()

    /**
     * Replaces the adapter's data and refreshes the RecyclerView.
     * Always call this instead of notifyDataSetChanged() directly.
     */
    fun updateData(newItems: List<Food>) {
        this.items = newItems.toList()
        notifyDataSetChanged()
    }

    fun toggleSelection(food: Food) {
        if (selectedFoods.containsKey(food.name)) {
            selectedFoods.remove(food.name)
        } else {
            selectedFoods[food.name] = food
        }
        notifyDataSetChanged()
    }

    fun selectFood(food: Food) {
        selectedFoods[food.name] = food
        notifyDataSetChanged()
    }

    fun isSelected(food: Food): Boolean = selectedFoods.containsKey(food.name)

    fun clearSelections() {
        selectedFoods.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_food_selectable, parent, false)
        return FoodViewHolder(view)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val food = items[position]
        val isSelected = selectedFoods.containsKey(food.name)

        holder.name.text = food.name
        
        // Use the new helper properties from Food class
        val displayFood = selectedFoods[food.name] ?: food
        holder.macros.text = "${displayFood.totalCalories} kcal • P ${displayFood.totalProtein}g • C ${displayFood.totalCarbs}g • F ${displayFood.totalFat}g"

        holder.checkbox.setOnCheckedChangeListener(null)
        holder.checkbox.isChecked = isSelected
        holder.checkbox.isClickable = false
        holder.checkbox.isFocusable = false

        holder.itemView.setOnClickListener {
            onItemClicked(food)
        }
    }

    override fun getItemCount(): Int = items.size

    class FoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkbox: CheckBox = itemView.findViewById(R.id.checkbox_select)
        val name: TextView = itemView.findViewById(R.id.text_food_name)
        val macros: TextView = itemView.findViewById(R.id.text_food_macros)
    }
}
