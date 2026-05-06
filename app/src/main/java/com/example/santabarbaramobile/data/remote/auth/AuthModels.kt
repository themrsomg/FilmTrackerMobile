package com.example.santabarbaramobile.data.remote.auth

import com.google.gson.annotations.SerializedName

data class LoginRequest(val email: String, val password: String)
data class ForgotPasswordRequest(val email: String)

data class UserDto(
    val id: String,
    val email: String,
    val role: String
)

data class RegisterResponse(
    val id: String,
    val email: String,
    val role: String
)

data class RegisterRequest(
    val email: String,
    val username: String,
    val password: String
)

data class ProfileState(
    val username: String = "",
    val email: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

data class UserResponse(
    @SerializedName("id") val id: String,
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String,
    @SerializedName("profilePicture") val profilePicture: String? = null
)

data class LoginResponse(
    @SerializedName("token") val token: String,
    @SerializedName("username") val username: String? = null,
    @SerializedName("email") val email: String? = null
)