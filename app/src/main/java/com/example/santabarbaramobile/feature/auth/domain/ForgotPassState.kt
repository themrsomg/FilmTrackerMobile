package com.example.santabarbaramobile.feature.auth.domain

sealed class ForgotPassState {
    object Idle : ForgotPassState()
    object Loading : ForgotPassState()
    object Success : ForgotPassState()
    data class Error(val message: String) : ForgotPassState()
}

data class ResendVerificationRequest(
    val email: String
)