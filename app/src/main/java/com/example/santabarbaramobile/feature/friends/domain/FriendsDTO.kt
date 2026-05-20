package com.example.santabarbaramobile.feature.friends.domain

import com.example.santabarbaramobile.feature.profile.domain.UserDto

data class FriendsSummaryDto(
    val authId: String,
    val friendsCount: Int? = 0
)

data class FriendRequestDto(
    val id: Int,
    val requester_auth_id: String,
    val receiver_auth_id: String,
    val status: String
)

data class FriendStatusResponse(
    val status: String,
    val relationship: FriendRequestDto?
)

data class FriendUIItem(
    val id: Int, val user: UserDto
)

data class RequestUIItem(
    val requestId: Int, val user: UserDto
)

data class FriendsManagerState(
    val isLoading: Boolean = true,
    val friends: List<FriendUIItem> = emptyList(),
    val incomingRequests: List<RequestUIItem> = emptyList(),
    val outgoingRequests: List<RequestUIItem> = emptyList(),
    val error: String? = null
)