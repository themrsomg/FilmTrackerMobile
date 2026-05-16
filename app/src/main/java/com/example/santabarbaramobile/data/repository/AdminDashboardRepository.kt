package com.example.santabarbaramobile.data.repository

import com.example.santabarbaramobile.data.remote.auth.AuthApi
import com.example.santabarbaramobile.data.remote.reviews.ReviewsApi
import com.example.santabarbaramobile.data.remote.moderation.ModerationApi
import com.example.santabarbaramobile.data.model.models.AuthStatsDto
import com.example.santabarbaramobile.data.model.models.ModerationStatsDto
import com.example.santabarbaramobile.data.model.models.ReviewStatsDto
import com.example.santabarbaramobile.data.security.TokenManager
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
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun getReviewStats(): Result<ReviewStatsDto> = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${tokenManager.getToken()}"
            val res = reviewsApi.getReviewStats(token)
            if (res.isSuccessful && res.body()?.data != null) Result.success(res.body()!!.data!!)
            else Result.failure(Exception("Error ReviewStats"))
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun getModerationStats(): Result<ModerationStatsDto> = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${tokenManager.getToken()}"
            val res = moderationApi.getModerationStats(token)
            if (res.isSuccessful && res.body()?.data != null) Result.success(res.body()!!.data!!)
            else Result.failure(Exception("Error ModerationStats"))
        } catch (e: Exception) { Result.failure(e) }
    }
}