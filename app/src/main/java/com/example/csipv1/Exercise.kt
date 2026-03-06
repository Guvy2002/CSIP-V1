package com.example.csipv1

data class Exercise(
    val id: Int,
    val name: String,
    val muscleGroup: String,
    val sets: Int,
    val reps: String,
    val instructions: List<String>,
    val videoUrl: String,
    var isCompleted: Int = 0
)