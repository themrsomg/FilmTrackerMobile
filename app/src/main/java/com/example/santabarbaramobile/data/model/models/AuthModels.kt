package com.example.santabarbaramobile.data.model.models

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    val email: String,
    val password: String
)

data class ForgotPasswordRequest(
    val email: String
)

data class ConfirmAccountRequest(
    val email: String,
    val code: String
)

data class VerifyEmailRequest(
    val email: String,
    val code: String
)

data class ApiResponse<T>(
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: T? = null
)

data class UserDto(
    val id: String,
    val email: String,
    val username: String? = null,
    val role: String? = "USER",
    @SerializedName("emailVerified") val emailVerified: Boolean = false
)

data class RegisterResponse(
    val id: String,
    val email: String,
    val username: String,
    val role: String,
    @SerializedName("emailVerified") val emailVerified: Boolean = false,
    @SerializedName("verificationEmailSent") val verificationEmailSent: Boolean = false
)

data class RegisterRequest(
    val email: String,
    val name: String,
    val username: String,
    val password: String,
    val profileImage: String? = null
)

data class UserResponse(
    @SerializedName("id") val id: String,
    @SerializedName("authId") val authId: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String,
    @SerializedName("profileImage") val profileImage: String? = null,
    @SerializedName("isEmailVerified") val isEmailVerified: Boolean = false,
    val role: String? = "USER"
)

data class LoginResponse(
    @SerializedName("token") val token: String,
    @SerializedName("user") val user: UserDto
)