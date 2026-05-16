package com.example.santabarbaramobile.data.model.dtos

import com.google.gson.annotations.SerializedName

data class TopUserDto(
    val authId: String,
    val username: String,
    val profileImage: String?,
    val totalScore: Int?
)

data class TopReviewDto(
    val id: Int,
    val tvmazeId: Int,
    val title: String,
    val content: String,
    val rating: Int,
    val likesCount: Int,
    val username: String
)

data class LeaderboardUsersResponse(
    @SerializedName("top") val data: List<TopUserDto>
)

data class LeaderboardReviewsResponse(
    @SerializedName("top") val data: List<TopReviewDto>
)