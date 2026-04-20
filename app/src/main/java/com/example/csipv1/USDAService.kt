package com.example.csipv1

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface USDAService {
    @GET("fdc/v1/foods/search")
    fun searchFood(
        @Query("query") query: String,
        @Query("api_key") apiKey: String = "DEMO_KEY",
        @Query("pageSize") pageSize: Int = 50,
        @Query("dataType") dataType: String = "Branded,Survey (FNDDS)"
    ): Call<USDASearchResponse>
}
