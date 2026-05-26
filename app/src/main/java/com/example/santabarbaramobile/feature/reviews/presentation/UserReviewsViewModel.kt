package com.example.santabarbaramobile.feature.reviews.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.santabarbaramobile.feature.reviews.domain.ModerationRepository
import com.example.santabarbaramobile.feature.reviews.domain.ReviewsRepository
import com.example.santabarbaramobile.feature.reviews.domain.UserReviewsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserReviewsViewModel @Inject constructor(
    private val reviewsRepository: ReviewsRepository,
    private val moderationRepository: ModerationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserReviewsUiState())
    val uiState: StateFlow<UserReviewsUiState> = _uiState.asStateFlow()

    fun loadUserReviews(userId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            reviewsRepository.getReviewsByUser(userId)
                .onSuccess { baseReviews ->
                    val hydratedReviews = baseReviews.map { baseReview ->
                        async {
                            try {
                                val showReviewsRes = reviewsRepository.getReviewsByShow(baseReview.tvmazeId, 1).getOrNull()
                                val realReview = showReviewsRes?.reviews?.find { it.id == baseReview.id }
                                realReview ?: baseReview
                            } catch (e: Exception) {
                                baseReview
                            }
                        }
                    }.awaitAll()

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        reviews = hydratedReviews
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.localizedMessage ?: "Error al cargar las reseñas"
                    )
                }
        }
    }

    fun toggleLike(reviewId: String, isCurrentlyLiked: Boolean) {
        _uiState.value = _uiState.value.copy(
            reviews = _uiState.value.reviews.map { review ->
                if (review.id == reviewId) {
                    review.copy(
                        likedByMe = !isCurrentlyLiked,
                        likesCount = review.likesCount + if (isCurrentlyLiked) -1 else 1
                    )
                } else review
            }
        )
        viewModelScope.launch {
            reviewsRepository.toggleLike(reviewId, isCurrentlyLiked)
                .onFailure {
                    _uiState.value = _uiState.value.copy(
                        reviews = _uiState.value.reviews.map { review ->
                            if (review.id == reviewId) {
                                review.copy(
                                    likedByMe = isCurrentlyLiked,
                                    likesCount = review.likesCount + if (isCurrentlyLiked) 1 else -1
                                )
                            } else review
                        }
                    )
                }
        }
    }
    fun reportReview(reviewId: String, reasonCode: String, description: String) {
        viewModelScope.launch {
            moderationRepository.createReport("REVIEW", reviewId, reasonCode, description)
        }
    }
}