package com.example.csipv1

data class User(
    val uid: String = "",
    val username: String = "",
    val email: String = "",
    val points: Int = 0,
    val friends: List<String> = listOf()
)
