package com.example.santabarbaramobile.feature.reviews.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.santabarbaramobile.feature.reviews.domain.ReviewsRepository
import com.example.santabarbaramobile.feature.reviews.domain.UserReviewsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserReviewsViewModel @Inject constructor(
    private val reviewsRepository: ReviewsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserReviewsUiState())
    val uiState: StateFlow<UserReviewsUiState> = _uiState.asStateFlow()

    fun loadUserReviews(userId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            reviewsRepository.getReviewsByUser(userId)
                .onSuccess { reviewsList ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        reviews = reviewsList
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
}