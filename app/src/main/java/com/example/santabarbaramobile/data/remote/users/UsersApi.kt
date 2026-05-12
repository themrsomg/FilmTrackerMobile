package com.example.santabarbaramobile.data.remote.users

import com.example.santabarbaramobile.data.remote.auth.UserResponse
import com.example.santabarbaramobile.data.remote.auth.ApiResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface UsersApi {

    @GET("api/users/profile")
    suspend fun getProfile(
        @Header("Authorization") token: String
    ): Response<ApiResponse<UserResponse>>

    @GET("api/users/{username}")
    suspend fun getUserByUsername(
        @Path("username") username: String
    ): Response<com.example.santabarbaramobile.data.model.UserSearchResponse>

    @GET("api/users/id/{authId}")
    suspend fun getUserById(
        @Path("authId") authId: String
    ): Response<com.example.santabarbaramobile.data.model.UserSearchResponse>
}
