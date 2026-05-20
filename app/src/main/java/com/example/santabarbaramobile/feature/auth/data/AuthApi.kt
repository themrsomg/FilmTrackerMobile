package com.example.santabarbaramobile.feature.auth.data

import com.example.santabarbaramobile.feature.profile.domain.AccountStatusResponse
import com.example.santabarbaramobile.feature.profile.domain.BanRequestDto
import com.example.santabarbaramobile.feature.auth.domain.ChangePasswordRequest
import com.example.santabarbaramobile.feature.auth.domain.ResetPasswordRequest
import com.example.santabarbaramobile.feature.profile.domain.SuspendRequestDto
import com.example.santabarbaramobile.feature.profile.domain.AdminStatsResponse
import com.example.santabarbaramobile.feature.auth.domain.ApiResponse
import com.example.santabarbaramobile.feature.profile.domain.AuthStatsDto
import com.example.santabarbaramobile.feature.auth.domain.ConfirmAccountRequest
import com.example.santabarbaramobile.feature.auth.domain.ForgotPasswordRequest
import com.example.santabarbaramobile.feature.auth.domain.LoginRequest
import com.example.santabarbaramobile.feature.auth.domain.LoginResponse
import com.example.santabarbaramobile.feature.auth.domain.RegisterRequest
import com.example.santabarbaramobile.feature.auth.domain.RegisterResponse
import com.example.santabarbaramobile.feature.auth.domain.VerifyEmailRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface AuthApi {
    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<ApiResponse<LoginResponse>>

    @POST("api/auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<ApiResponse<RegisterResponse>>

    @POST("api/auth/forgot-password")
    suspend fun forgotPassword(
        @Body request: ForgotPasswordRequest
    ): Response<Unit>

    @POST("auth/confirm-account")
    suspend fun confirmAccount(
        @Body request: ConfirmAccountRequest
    ): Response<Unit>

    @POST("api/auth/reset-password")
    suspend fun resetPassword(
        @Body request: ResetPasswordRequest
    ): Response<Unit>

    @PUT("api/auth/change-password")
    suspend fun changePassword(
        @Header("Authorization") token: String,
        @Body request: ChangePasswordRequest
    ) : Response<Unit>

    @POST("api/auth/verify-email")
    suspend fun verifyEmail(
        @Body request: VerifyEmailRequest
    ): Response<Unit>

    @GET("api/auth/admin/stats")
    suspend fun getAuthStats(
        @Header("Authorization") token: String
    ): Response<AdminStatsResponse<AuthStatsDto>>

    @GET("api/auth/admin/users/{authId}/status")
    suspend fun getAccountStatus(
        @Header("Authorization") token: String,
        @Path("authId") authId: String
    ): Response<AccountStatusResponse>

    @PATCH("api/auth/admin/users/{authId}/suspend")
    suspend fun suspendUser(
        @Header("Authorization") token: String,
        @Path("authId") authId: String,
        @Body request: SuspendRequestDto
    ): Response<Unit>

    @PATCH("api/auth/admin/users/{authId}/ban")
    suspend fun banUser(
        @Header("Authorization") token: String,
        @Path("authId") authId: String,
        @Body request: BanRequestDto
    ): Response<Unit>

    @PATCH("api/auth/admin/users/{authId}/unban")
    suspend fun unbanUser(
        @Header("Authorization") token: String,
        @Path("authId") authId: String
    ): Response<Unit>
}