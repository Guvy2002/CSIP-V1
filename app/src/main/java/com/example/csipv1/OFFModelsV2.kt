package com.example.csipv1

import com.google.gson.annotations.SerializedName

data class OFFV2Response(
    @SerializedName("products") val products: List<OFFV2Product>?
)

data class OFFV2Product(
    @SerializedName("product_name") val productName: String?,
    @SerializedName("brands") val brands: String?,
    @SerializedName("nutriments") val nutriments: OFFV2Nutriments?,
    @SerializedName("image_front_url") val imageUrl: String?
)

data class OFFV2Nutriments(
    @SerializedName("energy-kcal_100g") val calories: Double?,
    @SerializedName("proteins_100g") val protein: Double?,
    @SerializedName("carbohydrates_100g") val carbs: Double?,
    @SerializedName("fat_100g") val fat: Double?
)
