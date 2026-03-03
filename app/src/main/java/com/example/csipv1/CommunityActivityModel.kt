package com.example.csipv1

data class CommunityActivityModel(
    val id: String = "",
    val userId: String = "",
    val username: String = "User",
    val type: String = "MEAL", // "MEAL" or "WORKOUT"
    val content: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
