package com.example.santabarbaramobile.ui.auth.ViewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.santabarbaramobile.data.model.CommentDto
import com.example.santabarbaramobile.data.repository.CommentsRepository
import com.example.santabarbaramobile.data.repository.UserRepository
import com.example.santabarbaramobile.data.security.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
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

    fun loadComments(reviewId: String) {
        viewModelScope.launch {
            isLoading = true
            repository.getComments(reviewId)
                .onSuccess { comments = it }
                .onFailure { errorMessage = it.message }
            isLoading = false
        }
    }

    fun postComment(reviewId: String, content: String) {
        if (content.isBlank()) return

        viewModelScope.launch {
            isLoading = true
            repository.createComment(reviewId, content)
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
}