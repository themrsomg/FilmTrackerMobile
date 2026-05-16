package com.example.santabarbaramobile.ui.auth.ViewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.santabarbaramobile.data.model.dtos.TopReviewDto
import com.example.santabarbaramobile.data.model.dtos.TopUserDto
import com.example.santabarbaramobile.data.repository.LeaderboardsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LeaderboardsViewModel @Inject constructor(
    private val repository: LeaderboardsRepository
) : ViewModel() {

    var isLoading by mutableStateOf(true)
        private set
    var topUsers by mutableStateOf<List<TopUserDto>>(emptyList())
        private set
    var topReviews by mutableStateOf<List<TopReviewDto>>(emptyList())
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        loadLeaderboards()
    }

    fun loadLeaderboards() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            val usersDeferred = async { repository.getTopUsers() }
            val reviewsDeferred = async { repository.getTopReviews() }

            val usersResult = usersDeferred.await()
            val reviewsResult = reviewsDeferred.await()

            if (usersResult.isSuccess && reviewsResult.isSuccess) {
                topUsers = usersResult.getOrDefault(emptyList())
                topReviews = reviewsResult.getOrDefault(emptyList())
            } else {
                errorMessage = "Hubo un problema al cargar el salón de la fama."
            }

            isLoading = false
        }
    }
}