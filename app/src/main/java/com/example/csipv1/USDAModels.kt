package com.example.csipv1

import com.google.gson.annotations.SerializedName

data class USDASearchResponse(
    @SerializedName("foods") val foods: List<USDAFood>?
)

data class USDAFood(
    @SerializedName("description") val description: String?,
    @SerializedName("brandOwner") val brandOwner: String?,
    @SerializedName("foodNutrients") val foodNutrients: List<USDANutrient>?
)

data class USDANutrient(
    @SerializedName("nutrientName") val name: String?,
    @SerializedName("value") val value: Double?,
    @SerializedName("unitName") val unitName: String?
)
