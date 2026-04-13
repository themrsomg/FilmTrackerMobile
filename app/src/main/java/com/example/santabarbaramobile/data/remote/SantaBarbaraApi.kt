package com.example.santabarbaramobile.data.remote

import com.example.santabarbaramobile.data.model.HomeResponse
import retrofit2.http.GET

interface SantaBarbaraApi {
    @GET("api/shows/home")
    suspend fun getHomeData(): HomeResponse
}