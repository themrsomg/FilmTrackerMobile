package com.example.santabarbaramobile.data.model.dtos

import com.google.gson.annotations.SerializedName

data class CommentDto(
    @SerializedName("_id", alternate = ["id"]) val id: String,
    @SerializedName("authId", alternate = ["auth_id"]) val authId: String,
    val content: String,
    @SerializedName("imageUrl", alternate = ["image", "image_url"])
    val imageUrl: String?,
    @SerializedName("likesCount", alternate = ["likes_count"])
    val likesCount: Int = 0,
    @SerializedName("createdAt", alternate = ["created_at"]) val createdAt: String,
    @SerializedName("likedByMe", alternate = ["liked_by_me"]) val likedByMe: Boolean = false,
    val user: UserDto? = null
)

data class CommentPaginationResponse(
    val comments: List<CommentDto>
)

data class SingleCommentResponse(
    val comment: CommentDto
)