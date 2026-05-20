package com.example.santabarbaramobile.feature.reviews.data

import com.example.santabarbaramobile.feature.profile.domain.LeaderboardReviewsResponse
import com.example.santabarbaramobile.feature.profile.domain.LeaderboardUsersResponse
import retrofit2.Response
import retrofit2.http.GET

interface LeaderboardsApi {
    @GET("api/leaderboards/users")
    suspend fun getTopUsers(): Response<LeaderboardUsersResponse>

    @GET("api/leaderboards/reviews")
    suspend fun getTopReviews(): Response<LeaderboardReviewsResponse>
}