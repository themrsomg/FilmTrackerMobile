package com.example.santabarbaramobile.feature.profile.domain

import com.example.santabarbaramobile.feature.shows.domain.HomeResponse

sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(val data: HomeResponse) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}