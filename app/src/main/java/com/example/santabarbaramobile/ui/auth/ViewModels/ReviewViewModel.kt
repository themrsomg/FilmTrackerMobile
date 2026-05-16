package com.example.santabarbaramobile.ui.auth.ViewModels

import android.content.Context
import android.net.Uri
import android.util.Base64
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.santabarbaramobile.data.model.ReviewDto
import com.example.santabarbaramobile.data.repository.ModerationRepository
import com.example.santabarbaramobile.data.repository.ReviewsRepository
import com.example.santabarbaramobile.data.repository.UserRepository
import com.example.santabarbaramobile.data.security.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@HiltViewModel
class ReviewViewModel @Inject constructor(
    private val repository: ReviewsRepository,
    private val userRepository: UserRepository,
    private val moderationRepository: ModerationRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    var reviews by mutableStateOf<List<ReviewDto>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var currentUserId by mutableStateOf<String?>(null)
        private set

    var currentUserRole by mutableStateOf<String?>(null)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var isEmailVerified by mutableStateOf(false)
        private set

    var currentUserEmail by mutableStateOf("")
        private set

    init {
        loadCurrentUserId()
    }

    private fun loadCurrentUserId() {
        viewModelScope.launch {
            val token = tokenManager.getToken()
            if (token != null) {
                var realVerificationStatus = false
                try {
                    val parts = token.split(".")
                    if (parts.size == 3) {
                        val payload = String(Base64.decode(parts[1], Base64.URL_SAFE))
                        val jsonObject = JSONObject(payload)
                        realVerificationStatus = jsonObject.optBoolean("emailVerified", false)
                    }
                } catch (e: Exception) { /* Ignorar */ }

                userRepository.getUserProfile("Bearer $token").onSuccess { profile ->
                    currentUserId = profile.authId
                    currentUserRole = profile.role
                    isEmailVerified = realVerificationStatus
                    currentUserEmail = profile.email
                }
            }
        }
    }

    fun fetchReviews(tvmazeId: Int) {
        viewModelScope.launch {
            isLoading = true
            repository.getReviewsByShow(tvmazeId)
                .onSuccess { reviews = it.reviews }
                .onFailure { errorMessage = it.message }
            isLoading = false
        }
    }

    fun postReview(
        context: Context,
        tvmazeId: Int,
        rating: Int,
        title: String,
        content: String,
        imageUri: Uri?
    ) {
        viewModelScope.launch {
            isLoading = true
            val imagePart = imageUri?.let { uriToMultipart(context, it) }

            repository.createReview(tvmazeId, rating, title, content, imagePart)
                .onSuccess {
                    fetchReviews(tvmazeId)
                }
                .onFailure { errorMessage = it.message }
            isLoading = false
        }
    }

    fun updateReview(reviewId: String, tvmazeId: Int, rating: Int, title: String, content: String) {
        viewModelScope.launch {
            repository.updateReview(reviewId, rating, title, content)
                .onSuccess { fetchReviews(tvmazeId) }
                .onFailure { errorMessage = it.message }
        }
    }

    fun toggleLike(reviewId: String, isLiked: Boolean) {
        reviews = reviews.map { review ->
            if (review.id == reviewId) {
                review.copy(
                    likedByMe = !isLiked,
                    likesCount = review.likesCount + if (isLiked) -1 else 1
                )
            } else review
        }
        viewModelScope.launch {
            repository.toggleLike(reviewId, isLiked)
                .onFailure {
                    reviews = reviews.map { review ->
                        if (review.id == reviewId) {
                            review.copy(
                                likedByMe = isLiked,
                                likesCount = review.likesCount + if (isLiked) 1 else -1
                            )
                        } else review
                    }
                }
        }
    }

    fun deleteReview(reviewId: String, tvmazeId: Int) {
        viewModelScope.launch {
            isLoading = true
            repository.deleteReview(reviewId)
                .onSuccess { fetchReviews(tvmazeId) }
                .onFailure { errorMessage = it.message }
            isLoading = false
        }
    }

    private fun uriToMultipart(context: Context, uri: Uri): MultipartBody.Part? {
        val file = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
        context.contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(file).use { output -> input.copyTo(output) }
        }
        val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("image", file.name, requestFile)
    }

    fun reportReview(reviewId: String, reasonCode: String, description: String) {
        viewModelScope.launch {
            moderationRepository.createReport("REVIEW", reviewId, reasonCode, description)
        }
    }

    fun removeReviewImage(reviewId: String, tvmazeId: Int) {
    }
}