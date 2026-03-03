package com.example.csipv1

/**
 * Represents a single food item with its nutritional information and selection state.
 */
data class Food(
    val name: String,
    val calories: Int,
    val protein: Int = 0,
    val carbs: Int = 0,
    val fat: Int = 0,
    var isSelected: Boolean = false
)
