package com.example.csipv1

import com.google.gson.annotations.SerializedName

data class ExerciseDBResponse(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("target") val target: String,
    @SerializedName("bodyPart") val bodyPart: String,
    @SerializedName("equipment") val equipment: String,
    @SerializedName("gifUrl") val gifUrl: String,
    @SerializedName("instructions") val instructions: List<String>,
    @SerializedName("secondaryMuscles") val secondaryMuscles: List<String>
)
