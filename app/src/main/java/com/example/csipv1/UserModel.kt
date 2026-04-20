package com.example.csipv1

data class User(
    val uid: String = "",
    val username: String = "",
    val usernameLower: String = "",
    val email: String = "",
    val points: Int = 0,
    val friends: List<String> = listOf(),
    val lastPointsSteps: Long = 0,
    val dailySteps: Int = 0
)
