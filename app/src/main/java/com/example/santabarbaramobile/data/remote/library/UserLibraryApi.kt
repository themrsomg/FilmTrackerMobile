package com.example.santabarbaramobile.data.remote.library

import com.example.santabarbaramobile.data.model.dtos.CheckResponse
import com.example.santabarbaramobile.data.model.dtos.FavoritesResponse
import com.example.santabarbaramobile.data.model.dtos.LibraryRequest
import com.example.santabarbaramobile.data.model.models.LibraryListResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface UserLibraryApi {
    @POST("api/favorites")
    suspend fun addFavorite(
        @Header("Authorization") token: String,
        @Body request: LibraryRequest
    ): Response<Unit>

    @DELETE("api/favorites/{tvmazeId}")
    suspend fun removeFavorite(
        @Header("Authorization") token: String,
        @Path("tvmazeId") tvmazeId: String
    ): Response<Unit>

    @GET("api/favorites")
    suspend fun getFavorites(
        @Header("Authorization") token: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 49
    ): Response<FavoritesResponse>

    @POST("api/watchlist")
    suspend fun addToWatchlist(
        @Header("Authorization") token: String,
        @Body request: LibraryRequest
    ): Response<Unit>

    @DELETE("api/watchlist/{tvmazeId}")
    suspend fun removeFromWatchlist(
        @Header("Authorization") token: String,
        @Path("tvmazeId") tvmazeId: String
    ): Response<Unit>

    @GET("api/watchlist/{tvmazeId}/check")
    suspend fun checkIsInWatchlist(
        @Header("Authorization") token: String,
        @Path("tvmazeId") tvmazeId: String
    ): Response<CheckResponse>

    @GET("api/watchlist")
    suspend fun getMyWatchlist(
        @Header("Authorization") token: String
    ): Response<LibraryListResponse>

    @GET("api/favorites")
    suspend fun getMyFavorites(
        @Header("Authorization") token: String
    ): Response<LibraryListResponse>

    @GET("api/favorites/user/{authId}")
    suspend fun getUserFavorites(
        @Header("Authorization") token: String,
        @Path("authId") authId: String
    ): Response<LibraryListResponse>
}