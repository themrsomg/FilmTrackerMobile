package com.example.santabarbaramobile.ui.auth.States

sealed class ForgotPassState {
    object Idle : ForgotPassState()
    object Loading : ForgotPassState()
    object EmailSent : ForgotPassState()
    object CodeVerified : ForgotPassState()
    object Success : ForgotPassState()
    data class Error(val message: String) : ForgotPassState()
}