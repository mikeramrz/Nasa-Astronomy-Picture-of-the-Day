package com.example.nasaimage.data.remote

import com.example.nasaimage.data.model.ApodModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("planetary/apod")
    suspend fun getNasaImageOfTheDay(@Query("api_key") apiKey: String): ApodModel

    @GET("planetary/apod")
    suspend fun getNasaImageOfTheDayByDate(@Query("api_key") apiKey: String, @Query("date") date: String): Response<ApodModel>

}