package com.example.santabarbaramobile.data.remote.reviews

import com.example.santabarbaramobile.data.model.dtos.LeaderboardReviewsResponse
import com.example.santabarbaramobile.data.model.dtos.LeaderboardUsersResponse
import retrofit2.Response
import retrofit2.http.GET

interface LeaderboardsApi {
    @GET("api/leaderboards/users")
    suspend fun getTopUsers(): Response<LeaderboardUsersResponse>

    @GET("api/leaderboards/reviews")
    suspend fun getTopReviews(): Response<LeaderboardReviewsResponse>
}