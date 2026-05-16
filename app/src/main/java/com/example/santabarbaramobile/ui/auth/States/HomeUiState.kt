package com.example.santabarbaramobile.ui.auth.States

import com.example.santabarbaramobile.data.model.models.HomeResponse

sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(val data: HomeResponse) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}