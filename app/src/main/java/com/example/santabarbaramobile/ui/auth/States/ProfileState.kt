package com.example.santabarbaramobile.ui.auth.States

import com.example.santabarbaramobile.data.remote.library.LibraryItemDto

data class ProfileState(
    val name: String = "",
    val username: String = "",
    val email: String = "",
    val profileImage: String? = null,
    val isEmailVerified: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isOwnProfile: Boolean = true,
    val watchlist: List<LibraryItemDto> = emptyList(),
    val favorites: List<LibraryItemDto> = emptyList()
)