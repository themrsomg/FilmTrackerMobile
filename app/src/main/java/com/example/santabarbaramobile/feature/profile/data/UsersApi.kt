package com.example.santabarbaramobile.feature.profile.data

import com.example.santabarbaramobile.feature.profile.domain.UpdateProfileRequest
import com.example.santabarbaramobile.feature.profile.domain.UserDto
import com.example.santabarbaramobile.feature.profile.domain.UserSearchResponse
import com.example.santabarbaramobile.feature.auth.domain.ApiResponse
import com.example.santabarbaramobile.feature.auth.domain.UserResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface UsersApi {

    @GET("api/users/profile")
    suspend fun getProfile(
        @Header("Authorization") token: String
    ): Response<ApiResponse<UserResponse>>

    @GET("api/users/{username}")
    suspend fun getUserByUsername(
        @Path("username") username: String
    ): Response<UserSearchResponse>

    @GET("api/users/id/{authId}")
    suspend fun getUserById(
        @Path("authId") authId: String
    ): Response<UserSearchResponse>

    @PUT("api/users/profile")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Body request: UpdateProfileRequest
    ): Response<UserDto>

    @Multipart
    @POST("api/users/profile/photo")
    suspend fun uploadProfilePhoto(
        @Header("Authorization") token: String,
        @Part photo: MultipartBody.Part
    ): Response<UserDto>
}