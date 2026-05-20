package com.example.santabarbaramobile.feature.friends.domain

import com.example.santabarbaramobile.core.security.TokenManager
import com.example.santabarbaramobile.feature.profile.domain.AuthStatsDto
import com.example.santabarbaramobile.feature.profile.domain.ModerationStatsDto
import com.example.santabarbaramobile.feature.profile.domain.ReviewStatsDto
import com.example.santabarbaramobile.feature.profile.data.ModerationApi
import com.example.santabarbaramobile.feature.reviews.data.ReviewsApi
import com.example.santabarbaramobile.feature.auth.data.AuthApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdminDashboardRepository @Inject constructor(
    private val authApi: AuthApi,
    private val reviewsApi: ReviewsApi,
    private val moderationApi: ModerationApi,
    private val tokenManager: TokenManager
) {
    suspend fun getAuthStats(): Result<AuthStatsDto> = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${tokenManager.getToken()}"
            val res = authApi.getAuthStats(token)
            if (res.isSuccessful && res.body()?.data != null) Result.success(res.body()!!.data!!)
            else Result.failure(Exception("Error AuthStats"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getReviewStats(): Result<ReviewStatsDto> = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${tokenManager.getToken()}"
            val res = reviewsApi.getReviewStats(token)
            if (res.isSuccessful && res.body()?.data != null) Result.success(res.body()!!.data!!)
            else Result.failure(Exception("Error ReviewStats"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getModerationStats(): Result<ModerationStatsDto> = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${tokenManager.getToken()}"
            val res = moderationApi.getModerationStats(token)
            if (res.isSuccessful && res.body()?.data != null) Result.success(res.body()!!.data!!)
            else Result.failure(Exception("Error ModerationStats"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}