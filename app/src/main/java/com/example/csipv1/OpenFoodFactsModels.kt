package com.example.csipv1

import com.google.gson.annotations.SerializedName

data class OFFSearchResponse(
    @SerializedName("products") val products: List<OFFProduct>?
)

data class OFFProduct(
    @SerializedName("product_name") val productName: String?,
    @SerializedName("nutriments") val nutriments: Map<String, Any>?
)
