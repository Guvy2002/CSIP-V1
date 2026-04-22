package com.example.csipv1

data class CommunityActivityModel(
    val id: String = "",
    val userId: String = "",
    val username: String = "User",
    val type: String = "MEAL", // "MEAL" or "WORKOUT"
    val content: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val highFives: List<String> = emptyList(),
    val comments: List<CommentModel> = emptyList(),
    val sharable: Boolean = false,
    val dishName: String? = null,
    val calories: Int = 0,
    val protein: Int = 0,
    val carbs: Int = 0,
    val fat: Int = 0,
    val unit: String? = "serving"
)

data class CommentModel(
    val id: String = "",
    val userId: String = "",
    val username: String = "User",
    val comment: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
