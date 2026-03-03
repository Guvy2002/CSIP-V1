package com.example.csipv1

data class UserGoals(
    val age: Int = 0,
    val gender: String = "male", // "male" or "female"
    val heightCm: Double = 0.0,
    val weightKg: Double = 0.0,
    val activityLevel: String = "Sedentary",
    val fitnessGoal: String = "lose_fat", // "lose_fat", "build_muscle", "maintain"
    val dailyCalories: Int = 0,
    val protein: Int = 0,
    val carbs: Int = 0,
    val fats: Int = 0
)