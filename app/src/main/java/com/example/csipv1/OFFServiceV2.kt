package com.example.csipv1

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface OFFServiceV2 {
    /**
     * Updated to prefer English results and the UK market.
     */
    @GET("cgi/search.pl")
    fun searchFood(
        @Query("search_terms") query: String,
        @Header("User-Agent") userAgent: String = "CSIP_FoodTracker_Android - Version 1.0",
        @Query("lc") language: String = "en",
        @Query("cc") country: String = "uk",
        @Query("search_simple") searchSimple: Int = 1,
        @Query("action") action: String = "process",
        @Query("sort_by") sortBy: String = "unique_scans_n",
        @Query("json") json: Int = 1,
        @Query("page_size") pageSize: Int = 50,
        @Query("fields") fields: String = "product_name,brands,nutriments,image_front_url"
    ): Call<OFFV2Response>
}
