package com.example.santabarbaramobile.ui.auth.ViewModels

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.santabarbaramobile.data.model.ReviewDto
import com.example.santabarbaramobile.data.repository.ReviewsRepository
import com.example.santabarbaramobile.data.repository.UserRepository
import com.example.santabarbaramobile.data.security.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@HiltViewModel
class ReviewViewModel @Inject constructor(
    private val repository: ReviewsRepository,
    private val userRepository: UserRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    var reviews by mutableStateOf<List<ReviewDto>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var currentUserId by mutableStateOf<String?>(null)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        loadCurrentUserId()
    }

    private fun loadCurrentUserId() {
        viewModelScope.launch {
            val token = tokenManager.getToken()
            if (token != null) {
                userRepository.getUserProfile("Bearer $token").onSuccess { profile ->
                    currentUserId = profile.authId
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
        viewModelScope.launch {
            repository.toggleLike(reviewId, isLiked)
                .onSuccess { /* Actualizar contador localmente o recargar */ }
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
}