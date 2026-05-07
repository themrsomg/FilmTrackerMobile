package com.example.santabarbaramobile.data.remote.users

import com.example.santabarbaramobile.data.remote.auth.UserResponse
import retrofit2.http.GET
import retrofit2.http.Header

interface UsersApi {

    @GET("api/users/profile")
    suspend fun getProfile(
        @Header("Authorization") token: String
    ): UserResponse
}
