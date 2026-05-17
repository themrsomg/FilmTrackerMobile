package com.example.santabarbaramobile.data.remote.auth

import com.example.santabarbaramobile.data.model.dtos.AccountStatusResponse
import com.example.santabarbaramobile.data.model.models.AdminStatsResponse
import com.example.santabarbaramobile.data.model.models.AuthStatsDto
import com.example.santabarbaramobile.data.model.dtos.BanRequestDto
import com.example.santabarbaramobile.data.model.dtos.ChangePasswordRequest
import com.example.santabarbaramobile.data.model.dtos.ResetPasswordRequest
import com.example.santabarbaramobile.data.model.dtos.SuspendRequestDto
import com.example.santabarbaramobile.data.model.models.ApiResponse
import com.example.santabarbaramobile.data.model.models.ConfirmAccountRequest
import com.example.santabarbaramobile.data.model.models.ForgotPasswordRequest
import com.example.santabarbaramobile.data.model.models.LoginRequest
import com.example.santabarbaramobile.data.model.models.LoginResponse
import com.example.santabarbaramobile.data.model.models.RegisterRequest
import com.example.santabarbaramobile.data.model.models.RegisterResponse
import com.example.santabarbaramobile.data.model.models.VerifyEmailRequest
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
    ): retrofit2.Response<AccountStatusResponse>

    @PATCH("api/auth/admin/users/{authId}/suspend")
    suspend fun suspendUser(
        @Header("Authorization") token: String,
        @Path("authId") authId: String,
        @Body request: SuspendRequestDto
    ): retrofit2.Response<Unit>

    @PATCH("api/auth/admin/users/{authId}/ban")
    suspend fun banUser(
        @Header("Authorization") token: String,
        @Path("authId") authId: String,
        @Body request: BanRequestDto
    ): retrofit2.Response<Unit>

    @PATCH("api/auth/admin/users/{authId}/unban")
    suspend fun unbanUser(
        @Header("Authorization") token: String,
        @Path("authId") authId: String
    ): retrofit2.Response<Unit>
}
