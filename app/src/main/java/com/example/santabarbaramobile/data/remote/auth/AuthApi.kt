package com.example.santabarbaramobile.data.remote.auth

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<ApiResponse<LoginResponse>>

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<ApiResponse<RegisterResponse>>

    @POST("api/auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<Unit>

    @POST("auth/confirm-account")
    suspend fun confirmAccount(@Body request: ConfirmAccountRequest): Response<Unit>

    @POST("api/auth/verify-email")
    suspend fun verifyEmail(@Body request: VerifyEmailRequest): Response<Unit>
}
