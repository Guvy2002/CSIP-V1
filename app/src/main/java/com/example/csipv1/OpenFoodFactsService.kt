package com.example.csipv1

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface OpenFoodFactsService {
    @GET("cgi/search.pl?search_simple=1&action=process&json=1")
    fun searchFood(
        @Query("search_terms") query: String,
        @Header("User-Agent") userAgent: String,
        @Query("page_size") pageSize: Int = 20
    ): Call<OFFSearchResponse>
}
