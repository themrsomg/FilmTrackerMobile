package com.example.santabarbaramobile.feature.profile.domain

import com.google.gson.annotations.SerializedName

data class TopUserDto(
    val rank: Int,
    @SerializedName("auth_id", alternate = ["authId"]) val authId: String,
    @SerializedName("user_name", alternate = ["username"]) val username: String?,
    @SerializedName("profile_image", alternate = ["profileImage"]) val profileImage: String?,
    @SerializedName("total_likes", alternate = ["totalLikes", "totalScore"]) val totalScore: Int?
)

data class TopReviewDto(
    val rank: Int,
    @SerializedName("review_id", alternate = ["reviewId"]) val reviewId: String,
    @SerializedName("tvmaze_id", alternate = ["tvmazeId"]) val tvmazeId: Int,
    @SerializedName("auth_id", alternate = ["authId"]) val authId: String,
    @SerializedName("user_name", alternate = ["username"]) val username: String?,
    val title: String,
    val content: String,
    val rating: Int,
    @SerializedName("likes_count", alternate = ["likesCount"]) val likesCount: Int?
)

data class LeaderboardUsersResponse(
    @SerializedName("top") val data: List<TopUserDto>
)

data class LeaderboardReviewsResponse(
    @SerializedName("top") val data: List<TopReviewDto>
)