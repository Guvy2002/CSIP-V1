package com.example.csipv1

data class User(
    val uid: String = "",
    val username: String = "",
    val usernameLower: String = "",
    val email: String = "",
    val points: Int = 0,
    val friends: List<String> = listOf(),
    val friendRequests: List<FriendRequest> = listOf(),
    val sentRequests: List<SentRequest> = listOf(),
    val mutedFriends: Map<String, Long> = mapOf(),
    val lastPointsSteps: Long = 0,
    val dailySteps: Int = 0,
    val memberSince: Long = System.currentTimeMillis()
)

data class FriendRequest(
    val fromUid: String = "",
    val fromUsername: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

data class SentRequest(
    val toUid: String = "",
    val toEmail: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
