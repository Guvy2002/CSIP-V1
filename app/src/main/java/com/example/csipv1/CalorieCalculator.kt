package com.example.csipv1
object CalorieCalculator {
    //Calculates the TDEE (Total Daily Energy Expenditure)
    fun calculateNutrition(
        age: Int,
        gender: String,
        weightKg: Double,
        heightCm: Double,
        activityLevel: String,
        fitnessGoal: String
    ): NutritionPlan {
        //Calculate BMR (Mifflin-St Jeor Equation)
        val bmr = if (gender.lowercase() == "male") {
            (10 * weightKg) + (6.25 * heightCm) - (5 * age) + 5
        } else {
            (10 * weightKg) + (6.25 * heightCm) - (5 * age) - 161
        }

        //adjusted for activity level
        val activityFactor = when (activityLevel) {
            "Sedentary" -> 1.2
            "Lightly Active" -> 1.375
            "Moderately Active" -> 1.55
            "Very Active" -> 1.725
            else -> 1.2
        }
        val tdee = bmr * activityFactor

        //adjusted for fitness goal
        val dailyCalories = when (fitnessGoal) {
            "Lose Weight" -> tdee - 500
            "Maintain Weight" -> tdee
            "Gain Weight" -> tdee + 500
            else -> tdee
        }.toInt()

        // macro calculations
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
