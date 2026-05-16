package com.example.santabarbaramobile.data.model.models

import com.google.gson.annotations.SerializedName

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

data class SendFriendRequest(
    @SerializedName("receiverAuthId", alternate = ["receiver_auth_id"])
    val receiverAuthId: String
)

data class FriendItemDto(
    val id: Int,
    @SerializedName("friendAuthId", alternate = ["friend_auth_id"])
    val friendAuthId: String
)

data class FriendPaginationResponse(
    val data: List<FriendItemDto>
)

data class FriendRequestItemDto(
    val id: Int,
    @SerializedName("requesterAuthId", alternate = ["requester_auth_id"])
    val requesterAuthId: String,
    @SerializedName("receiverAuthId", alternate = ["receiver_auth_id"])
    val receiverAuthId: String,
    val status: String,
    @SerializedName("createdAt", alternate = ["created_at"])
    val createdAt: String
)

data class FriendRequestPaginationResponse(
    val data: List<FriendRequestItemDto>
)