package com.example.csipv1

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface OFFServiceV2 {
    /**
     * Modern Open Food Facts search.
     * Fixed to use the reliable v2 search endpoint.
     */
    @GET("api/v2/search")
    fun searchFood(
        @Query("search_terms") query: String,
        @Header("User-Agent") userAgent: String = "CSIPV1 - Android - Version 1.0",
        @Query("lc") language: String = "en",
        @Query("cc") country: String = "uk",
        @Query("json") json: Int = 1,
        @Query("page_size") pageSize: Int = 50,
        @Query("fields") fields: String = "product_name,brands,nutriments,image_front_url"
    ): Call<OFFV2Response>
}
