package com.example.santabarbaramobile.data.repository

import com.example.santabarbaramobile.data.model.dtos.ReviewDto
import com.example.santabarbaramobile.data.model.dtos.ReviewPaginationResponse
import com.example.santabarbaramobile.data.model.dtos.UpdateReviewRequest
import com.example.santabarbaramobile.data.remote.reviews.ReviewsApi
import com.example.santabarbaramobile.data.security.TokenManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReviewsRepository @Inject constructor(
    private val api: ReviewsApi,
    private val tokenManager: TokenManager
) {
    suspend fun getReviewsByShow(tvmazeId: Int, page: Int = 1): Result<ReviewPaginationResponse> = withContext(Dispatchers.IO) {
        try {
            val token = tokenManager.getToken()?.let { "Bearer $it" }
            val response = api.getReviewsByShow(token, tvmazeId, page)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else Result.failure(Exception("Error al cargar reseñas"))
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun createReview(
        tvmazeId: Int,
        rating: Int,
        title: String,
        content: String,
        imagePart: MultipartBody.Part?
    ): Result<ReviewDto> = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${tokenManager.getToken()}"

            val rbTvmazeId = tvmazeId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val rbRating = rating.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val rbTitle = title.toRequestBody("text/plain".toMediaTypeOrNull())
            val rbContent = content.toRequestBody("text/plain".toMediaTypeOrNull())

            val response = api.createReview(token, rbTvmazeId, rbRating, rbTitle, rbContent, imagePart)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.review)
            } else {
                val errorString = response.errorBody()?.string()
                val errorMsg = try { JSONObject(errorString ?: "").getString("message") } catch (e: Exception) { "Error al publicar la reseña" }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun toggleLike(reviewId: String, isCurrentlyLiked: Boolean): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${tokenManager.getToken()}"
            val response = if (isCurrentlyLiked) {
                api.unlikeReview(token, reviewId)
            } else {
                api.likeReview(token, reviewId)
            }

            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("Error al procesar el like"))
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun updateReview(
        reviewId: String,
        rating: Int,
        title: String,
        content: String
    ): Result<ReviewDto> = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${tokenManager.getToken()}"
            val request = UpdateReviewRequest(rating, title, content)
            val response = api.updateReview(token, reviewId, request)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.review)
            } else {
                val errorMsg = try {
                    JSONObject(response.errorBody()?.string() ?: "").getString("message")
                } catch (e: Exception) { "Error al editar la reseña" }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun deleteReview(reviewId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${tokenManager.getToken()}"
            val response = api.deleteReview(token, reviewId)
            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("Error al borrar la reseña"))
        } catch (e: Exception) { Result.failure(e) }
    }
}