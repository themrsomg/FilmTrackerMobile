package com.example.santabarbaramobile.data.remote

import com.example.santabarbaramobile.data.model.HomeResponse
import com.example.santabarbaramobile.data.model.Show
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.Response

interface SantaBarbaraApi {
    @GET("api/shows/home")
    suspend fun getHomeData(): HomeResponse

    @GET("api/shows/{id}")
    suspend fun getShowById(@Path("id") showId: String): Response<Show>
}