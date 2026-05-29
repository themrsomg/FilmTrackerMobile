package com.example.santabarbaramobile.feature.reviews.domain

import com.example.santabarbaramobile.core.security.TokenManager
import com.example.santabarbaramobile.feature.reviews.data.CommentsApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommentsRepository @Inject constructor(
    private val api: CommentsApi,
    private val tokenManager: TokenManager
) {
    suspend fun getComments(reviewId: String): Result<List<CommentDto>> =
        withContext(Dispatchers.IO) {
            try {
                val token = tokenManager.getToken()?.let { "Bearer $it" }
                val response = api.getComments(token, reviewId)
                if (response.isSuccessful) Result.success(response.body()?.comments ?: emptyList())
                else Result.failure(Exception("Error al cargar comentarios"))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun createComment(reviewId: String, content: String, imagePart: MultipartBody.Part?): Result<CommentDto> =
        withContext(Dispatchers.IO) {
            try {
                val token = "Bearer ${tokenManager.getToken()}"
                val rbContent = content.toRequestBody("text/plain".toMediaTypeOrNull())
                val response = api.createComment(token, reviewId, rbContent, imagePart)
                if (response.isSuccessful && response.body() != null) Result.success(response.body()!!.comment)
                else Result.failure(Exception("Error al comentar"))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun deleteComment(commentId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${tokenManager.getToken()}"
            val response = api.deleteComment(token, commentId)
            if (response.isSuccessful) Result.success(Unit) else Result.failure(Exception("Error al borrar"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun toggleCommentLike(commentId: String, isCurrentlyLiked: Boolean): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val token = "Bearer ${tokenManager.getToken()}"
                val response = if (isCurrentlyLiked) api.unlikeComment(token, commentId)
                               else api.likeComment(token, commentId)
                if (response.isSuccessful) Result.success(Unit)
                else Result.failure(Exception("Error al procesar el like"))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun removeCommentImage(commentId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${tokenManager.getToken()}"
            val res = api.removeCommentImage(token, commentId)
            if (res.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al remover imagen del comentario (${res.code()})"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}