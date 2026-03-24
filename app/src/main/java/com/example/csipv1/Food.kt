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
    var isSelected: Boolean = false,
    var quantity: Double = 1.0,
    val unit: String = "serving"
) {
    // Helper methods to get values based on quantity
    val totalCalories: Int get() = (calories * quantity).toInt()
    val totalProtein: Int get() = (protein * quantity).toInt()
    val totalCarbs: Int get() = (carbs * quantity).toInt()
    val totalFat: Int get() = (fat * quantity).toInt()
}
