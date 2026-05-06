package com.example.santabarbaramobile.ui.auth.States

data class ProfileState(
    val username: String = "",
    val email: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)