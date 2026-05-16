package com.example.santabarbaramobile.ui.auth.ViewModels

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.santabarbaramobile.data.model.dtos.CommentDto
import com.example.santabarbaramobile.data.repository.CommentsRepository
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
class ReviewDetailViewModel @Inject constructor(
    private val repository: CommentsRepository,
    private val userRepository: UserRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    var comments by mutableStateOf<List<CommentDto>>(emptyList())
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

    var currentUserEmail by mutableStateOf("")
        private set

    init { loadCurrentUserId() }

    private fun loadCurrentUserId() {
        viewModelScope.launch {
            val token = tokenManager.getToken()
            if (token != null) {
                userRepository.getUserProfile("Bearer $token").onSuccess { profile ->
                    currentUserId = profile.authId
                    currentUserRole = profile.role
                    isEmailVerified = profile.isEmailVerified
                    currentUserEmail = profile.email
                }
            }
        }
    }

    fun loadComments(reviewId: String) {
        viewModelScope.launch {
            isLoading = true
            repository.getComments(reviewId)
                .onSuccess { comments = it }
                .onFailure { errorMessage = it.message }
            isLoading = false
        }
    }

    fun postComment(context: Context, reviewId: String, content: String, imageUri: Uri?) {
        if (content.isBlank() && imageUri == null) return

        viewModelScope.launch {
            isLoading = true
            val imagePart = imageUri?.let { uriToMultipart(context, it) }

            repository.createComment(reviewId, content, imagePart)
                .onSuccess { loadComments(reviewId) }
                .onFailure { errorMessage = it.message }
            isLoading = false
        }
    }

    fun deleteComment(commentId: String, reviewId: String) {
        viewModelScope.launch {
            repository.deleteComment(commentId)
                .onSuccess { loadComments(reviewId) }
                .onFailure { errorMessage = it.message }
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