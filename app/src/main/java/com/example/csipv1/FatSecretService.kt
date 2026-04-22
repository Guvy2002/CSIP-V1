//FSS API
package com.example.csipv1

import retrofit2.Call
import retrofit2.http.*

interface FatSecretService {
    @FormUrlEncoded
    @POST("connect/token")
    fun getAccessToken(
        @Header("Authorization") authHeader: String,
        @Field("grant_type") grantType: String = "client_credentials",
        @Field("scope") scope: String = "basic"
    ): Call<FatSecretTokenResponse>

    @GET("rest/server.api")
    fun searchFood(
        @Header("Authorization") bearerToken: String,
        @Query("method") method: String = "foods.search",
        @Query("search_expression") query: String,
        @Query("format") format: String = "json"
    ): Call<FatSecretSearchResponse>
}

data class FatSecretTokenResponse(
    val access_token: String,
    val expires_in: Int,
    val token_type: String
)

data class FatSecretSearchResponse(
    val foods: FatSecretFoodsResult?
)

data class FatSecretFoodsResult(
    val food: List<FatSecretFoodItem>?
)

data class FatSecretFoodItem(
    val food_name: String,
    val food_description: String,
    val food_id: String
)
