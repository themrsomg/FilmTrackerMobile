package com.example.santabarbaramobile.feature.profile.domain

import com.google.gson.annotations.SerializedName

data class NotificationDto(
    val id: Int,
    @SerializedName("recipient_auth_id") val recipientAuthId: String,
    @SerializedName("actor_auth_id") val actorAuthId: String?,
    val type: String,
    val title: String,
    val body: String,
    val metadata: NotificationMetadata?,
    @SerializedName("read_at") val readAt: String?,
    @SerializedName("created_at") val createdAt: String
) {
    val isRead: Boolean get() = readAt != null
    val message: String get() = body
    val senderUsername: String? get() = if (actorAuthId == null) "Sistema" else "Usuario"
    val senderAvatar: String? get() = null
    val relatedEntityId: String? get() = metadata?.reviewId?.toString() ?: metadata?.requestId?.toString()
}

data class NotificationMetadata(
    val reviewId: Int?,
    val tvmazeId: Int?,
    val requestId: Int?,
    val receiverAuthId: String?,
    val requesterAuthId: String?,
    val source: String?
)

data class NotificationPaginationResponse(
    @SerializedName("data") val notifications: List<NotificationDto>,
    val pagination: Any?
)

data class UnreadCountResponse(
    val unreadCount: Int
)