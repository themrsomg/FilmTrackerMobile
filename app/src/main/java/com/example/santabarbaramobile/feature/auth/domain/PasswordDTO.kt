package com.example.santabarbaramobile.feature.auth.domain

import com.google.gson.annotations.SerializedName

data class ResetPasswordRequest(
    val email: String,
    val code: String,
    @SerializedName("password") val newPassword: String
)

data class ChangePasswordRequest(
    val currentPassword: String,
    val newPassword: String
)