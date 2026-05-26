package com.example.santabarbaramobile.feature.reviews.domain

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import java.lang.reflect.Type

class CountDeserializer : JsonDeserializer<Int> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Int {
        return try {
            when {
                json == null -> 0
                json.isJsonPrimitive -> json.asInt
                json.isJsonArray -> json.asJsonArray.size()
                else -> 0
            }
        } catch (e: Exception) {
            0
        }
    }
}

data class ReviewDto(
    @SerializedName("_id", alternate = ["id"])
    val id: String,
    @SerializedName("authId", alternate = ["auth_id"])
    val authId: String,
    @SerializedName("tvmazeId", alternate = ["tvmaze_id"])
    val tvmazeId: Int,
    val rating: Int,
    val title: String,
    val content: String,
    @SerializedName("imageUrl", alternate = ["image", "image_url", "profileImage"])
    val imageUrl: String?,
    @SerializedName("likesCount", alternate = ["likes_count", "likes"])
    @JsonAdapter(CountDeserializer::class)
    val likesCount: Int = 0,
    @SerializedName("commentsCount", alternate = ["comments_count", "comments"])
    @JsonAdapter(CountDeserializer::class)
    val commentsCount: Int = 0,
    @SerializedName("createdAt", alternate = ["created_at"])
    val createdAt: String,
    @SerializedName("updatedAt", alternate = ["updated_at"])
    val updatedAt: String,
    @SerializedName("likedByMe", alternate = ["liked_by_me", "isLiked"])
    val likedByMe: Boolean = false
)

data class ReviewPaginationResponse(
    val reviews: List<ReviewDto>,
    val pagination: Any?
)

data class SingleReviewResponse(
    val message: String,
    val review: ReviewDto
)

data class ReviewSummaryDto(
    val authId: String,
    val reviewsCount: Int,
    val totalLikesReceived: Int
)

data class UpdateReviewRequest(
    val rating: Int,
    val title: String,
    val content: String
)

data class UserReviewsUiState(
    val isLoading: Boolean = false,
    val reviews: List<ReviewDto> = emptyList(),
    val error: String? = null
)