package com.example.santabarbaramobile.ui.auth.ViewModels

import android.content.Context
import android.net.Uri
import android.util.Base64
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.santabarbaramobile.data.model.dtos.CommentDto
import com.example.santabarbaramobile.data.model.dtos.ReviewDto
import com.example.santabarbaramobile.data.model.dtos.UserDto
import com.example.santabarbaramobile.data.repository.CommentsRepository
import com.example.santabarbaramobile.data.repository.ModerationRepository
import com.example.santabarbaramobile.data.repository.ReviewsRepository
import com.example.santabarbaramobile.data.repository.UserRepository
import com.example.santabarbaramobile.data.security.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

data class CommentUIItem(val comment: CommentDto, val user: UserDto?)

@HiltViewModel
class ReviewDetailViewModel @Inject constructor(
    private val commentsRepository: CommentsRepository,
    private val reviewsRepository: ReviewsRepository,
    private val userRepository: UserRepository,
    private val moderationRepository: ModerationRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    var mainReview by mutableStateOf<ReviewDto?>(null)
        private set

    var commentsUI by mutableStateOf<List<CommentUIItem>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var currentUserId by mutableStateOf<String?>(null)
        private set

    var currentUserRole by mutableStateOf<String?>(null)
        private set

    var isEmailVerified by mutableStateOf(false)
        private set

    init { loadCurrentUserId() }

    private fun loadCurrentUserId() {
        viewModelScope.launch {
            val token = tokenManager.getToken()
            if (token != null) {
                var verified = false
                try {
                    val parts = token.split(".")
                    if (parts.size == 3) {
                        val payload = String(Base64.decode(parts[1], Base64.URL_SAFE))
                        verified = JSONObject(payload).optBoolean("emailVerified", false)
                    }
                } catch (e: Exception) { }

                userRepository.getUserProfile("Bearer $token").onSuccess { profile ->
                    currentUserId = profile.authId
                    currentUserRole = profile.role
                    isEmailVerified = verified
                }
            }
        }
    }

    fun loadData(reviewId: String) {
        viewModelScope.launch {
            isLoading = true

            reviewsRepository.getReviewById(reviewId).onSuccess { mainReview = it }
            commentsRepository.getComments(reviewId)
                .onSuccess { commentsList ->
                    val hydrated = commentsList.map { comment ->
                        async {
                            val user = userRepository.getUserById(comment.authId ?: "").getOrNull()
                            CommentUIItem(comment, user)
                        }
                    }.awaitAll()
                    commentsUI = hydrated
                }
                .onFailure { errorMessage = it.message }
            isLoading = false
        }
    }

    fun postComment(context: Context, reviewId: String, content: String, imageUri: Uri?) {
        if (content.isBlank() && imageUri == null) return

        viewModelScope.launch {
            isLoading = true
            val imagePart = imageUri?.let { uriToMultipart(context, it) }

            commentsRepository.createComment(reviewId, content, imagePart)
                .onSuccess { loadData(reviewId) }
                .onFailure { errorMessage = it.message }
            isLoading = false
        }
    }

    fun deleteComment(commentId: String, reviewId: String) {
        viewModelScope.launch {
            commentsRepository.deleteComment(commentId)
                .onSuccess { loadData(reviewId) }
                .onFailure { errorMessage = it.message }
        }
    }

    fun removeCommentImage(commentId: String, reviewId: String) {
        viewModelScope.launch {
            commentsRepository.removeCommentImage(commentId)
                .onSuccess { loadData(reviewId) }
        }
    }

    fun reportComment(commentId: String, reason: String, desc: String) {
        viewModelScope.launch {
            moderationRepository.createReport("COMMENT", commentId, reason, desc)
        }
    }

    private fun uriToMultipart(context: Context, uri: Uri): MultipartBody.Part? {
        val file = File(context.cacheDir, "temp_comment_${System.currentTimeMillis()}.jpg")
        context.contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(file).use { output -> input.copyTo(output) }
        }
        val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("image", file.name, requestFile)
    }
}