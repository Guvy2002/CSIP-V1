package com.example.csipv1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FoodAdapter(
    private val items: List<Food>
) : RecyclerView.Adapter<FoodAdapter.FoodViewHolder>() {

    private val selectedPositions = mutableSetOf<Int>()

    fun getSelectedFoods(): List<Food> {
        return selectedPositions.map { items[it] }
    }

    /**
     * Pre-selects an item at the given position.
     */
    fun preSelect(position: Int) {
        if (position in items.indices) {
            selectedPositions.add(position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_food_selectable, parent, false)
        return FoodViewHolder(view)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val food = items[position]
        val isSelected = selectedPositions.contains(position)

        holder.name.text = food.name
        holder.macros.text = "${food.calories} kcal • P ${food.protein}g • C ${food.carbs}g • F ${food.fat}g"
        
        holder.checkbox.setOnCheckedChangeListener(null)
        holder.checkbox.isChecked = isSelected

        holder.itemView.setOnClickListener {
            toggle(position)
            notifyItemChanged(position)
        }

        holder.checkbox.setOnCheckedChangeListener { _, checked ->
            if (checked) {
                selectedPositions.add(position)
            } else {
                selectedPositions.remove(position)
            }
        }
    }

    override fun getItemCount(): Int = items.size

    private fun toggle(position: Int) {
        if (selectedPositions.contains(position)) {
            selectedPositions.remove(position)
        } else {
            selectedPositions.add(position)
        }
    }

    class FoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkbox: CheckBox = itemView.findViewById(R.id.checkbox_select)
        val name: TextView = itemView.findViewById(R.id.text_food_name)
        val macros: TextView = itemView.findViewById(R.id.text_food_macros)
    }
}
