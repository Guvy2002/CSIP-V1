package com.example.csipv1

/**
 * Helper class to calculate daily nutritional requirements.
 */
object CalorieCalculator {

    /**
     * Calculates the TDEE (Total Daily Energy Expenditure) and resulting macro targets.
     */
    fun calculateNutrition(
        age: Int,
        gender: String,
        weightKg: Double,
        heightCm: Double,
        activityLevel: String,
        fitnessGoal: String
    ): NutritionPlan {
        // 1. Calculate BMR (Mifflin-St Jeor Equation)
        val bmr = if (gender.lowercase() == "male") {
            (10 * weightKg) + (6.25 * heightCm) - (5 * age) + 5
        } else {
            (10 * weightKg) + (6.25 * heightCm) - (5 * age) - 161
        }

        // 2. Adjust for Activity Level
        val activityFactor = when (activityLevel) {
            "Sedentary" -> 1.2
            "Lightly Active" -> 1.375
            "Moderately Active" -> 1.55
            "Very Active" -> 1.725
            else -> 1.2
        }
        val tdee = bmr * activityFactor

        // 3. Adjust for Fitness Goal
        val dailyCalories = when (fitnessGoal) {
            "Lose Weight" -> tdee - 500
            "Maintain Weight" -> tdee
            "Gain Weight" -> tdee + 500
            else -> tdee
        }.toInt()

        // 4. Calculate Macros (Protein: 30%, Carbs: 40%, Fat: 30%)
        // Protein: 4 kcal/g, Carbs: 4 kcal/g, Fat: 9 kcal/g
        val protein = (dailyCalories * 0.30 / 4).toInt()
        val carbs = (dailyCalories * 0.40 / 4).toInt()
        val fat = (dailyCalories * 0.30 / 9).toInt()

        return NutritionPlan(dailyCalories, protein, carbs, fat)
    }

    data class NutritionPlan(
        val calories: Int,
        val protein: Int,
        val carbs: Int,
        val fat: Int
    )
}
