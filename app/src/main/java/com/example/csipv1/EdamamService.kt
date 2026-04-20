package com.example.csipv1

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface EdamamService {
    @GET("api/food-database/v2/parser")
    fun searchFood(
        @Query("ingr") query: String,
        @Query("app_id") appId: String,
        @Query("app_key") appKey: String
    ): Call<EdamamResponse>
}

data class EdamamResponse(
    val hints: List<EdamamHint>
)

data class EdamamHint(
    val food: EdamamFood
)

data class EdamamFood(
    val label: String,
    val nutrients: EdamamNutrients,
    val image: String?
)

data class EdamamNutrients(
    @SerializedName("ENERC_KCAL") val calories: Double,
    @SerializedName("PROCNT") val protein: Double,
    @SerializedName("FAT") val fat: Double,
    @SerializedName("CHOCDF") val carbs: Double
)
