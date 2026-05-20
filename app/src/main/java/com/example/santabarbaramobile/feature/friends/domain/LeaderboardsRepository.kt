package com.example.santabarbaramobile.feature.friends.domain

import com.example.santabarbaramobile.feature.profile.domain.TopReviewDto
import com.example.santabarbaramobile.feature.profile.domain.TopUserDto
import com.example.santabarbaramobile.feature.reviews.data.LeaderboardsApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LeaderboardsRepository @Inject constructor(
    private val api: LeaderboardsApi
) {
    suspend fun getTopUsers(): Result<List<TopUserDto>> = withContext(Dispatchers.IO) {
        try {
            val res = api.getTopUsers()
            if (res.isSuccessful && res.body()?.data != null) {
                Result.success(res.body()!!.data)
            } else {
                Result.failure(Exception("Error al cargar usuarios top"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTopReviews(): Result<List<TopReviewDto>> = withContext(Dispatchers.IO) {
        try {
            val res = api.getTopReviews()
            if (res.isSuccessful && res.body()?.data != null) {
                Result.success(res.body()!!.data)
            } else {
                Result.failure(Exception("Error al cargar reseñas top"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}