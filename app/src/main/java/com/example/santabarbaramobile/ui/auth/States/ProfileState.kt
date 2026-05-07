package com.example.santabarbaramobile.ui.auth.States

data class ProfileState(
    val name: String = "",
    val username: String = "",
    val email: String = "",
    val profileImage: String? = null,
    val isEmailVerified: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)
