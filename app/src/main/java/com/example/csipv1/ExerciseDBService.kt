package com.example.csipv1

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface ExerciseDBService {
    @GET("exercises")
    fun getAllExercises(
        @Header("x-rapidapi-key") apiKey: String,
        @Header("x-rapidapi-host") host: String = "exercisedb.p.rapidapi.com",
        @Query("limit") limit: Int = 20
    ): Call<List<ExerciseDBResponse>>

    @GET("exercises/bodyPart/{bodyPart}")
    fun getExercisesByBodyPart(
        @Path("bodyPart") bodyPart: String,
        @Header("x-rapidapi-key") apiKey: String,
        @Header("x-rapidapi-host") host: String = "exercisedb.p.rapidapi.com",
        @Query("limit") limit: Int = 20
    ): Call<List<ExerciseDBResponse>>
}
