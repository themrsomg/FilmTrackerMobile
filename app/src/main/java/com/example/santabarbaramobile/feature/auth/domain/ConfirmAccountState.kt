package com.example.santabarbaramobile.feature.auth.domain

sealed class ConfirmAccountState {
    object Idle : ConfirmAccountState()
    object Loading : ConfirmAccountState()
    data class Error(val message: String) : ConfirmAccountState()
    data class Success(val message: String) : ConfirmAccountState()
    data class ResendSuccess(val message: String) : ConfirmAccountState()
}