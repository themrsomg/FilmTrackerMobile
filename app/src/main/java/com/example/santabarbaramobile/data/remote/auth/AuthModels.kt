package com.example.santabarbaramobile.data.remote.auth

data class LoginRequest(val email: String, val password: String)
data class ForgotPasswordRequest(val email: String)

data class LoginResponse(
    val user: UserDto,
    val token: String
)

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