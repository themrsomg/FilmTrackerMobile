package com.example.santabarbaramobile.data.model.dtos

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