package com.example.santabarbaramobile.feature.profile.domain

import com.example.santabarbaramobile.feature.shows.domain.LibraryItemDto

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
    val favorites: List<LibraryItemDto> = emptyList(),
    val targetAuthId: String = "",
    val friendshipStatus: String = "NONE",
    val currentUserRole: String = "USER",
    val targetAccountStatus: String = "ACTIVE",
    val banSuccessMessage: String? = null
)