package com.example.santabarbaramobile.feature.auth.domain

sealed class ConfirmAccountState {
    object Idle : ConfirmAccountState()
    object Loading : ConfirmAccountState()
    object Success : ConfirmAccountState()
    data class Error(val message: String) : ConfirmAccountState()
}