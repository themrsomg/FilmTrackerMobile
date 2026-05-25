package com.example.santabarbaramobile.feature.profile.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.santabarbaramobile.feature.profile.domain.TopReviewDto
import com.example.santabarbaramobile.feature.profile.domain.TopUserDto
import com.example.santabarbaramobile.feature.friends.domain.LeaderboardsRepository
import com.example.santabarbaramobile.feature.profile.domain.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LeaderboardsViewModel @Inject constructor(
    private val repository: LeaderboardsRepository,
    private val userRepository: UserRepository
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
                val rawUsers = usersResult.getOrDefault(emptyList())
                val hydratedUsers = rawUsers.map { user ->
                    async {
                        val profile = userRepository.getUserById(user.authId).getOrNull()
                        user.copy(
                            username = profile?.username ?: "Usuario",
                            profileImage = profile?.profileImage
                        )
                    }
                }.awaitAll()
                val rawReviews = reviewsResult.getOrDefault(emptyList())
                val hydratedReviews = rawReviews.map { review ->
                    async {
                        val profile = userRepository.getUserById(review.authId).getOrNull()
                        review.copy(
                            username = profile?.username ?: "Usuario"
                        )
                    }
                }.awaitAll()
                topUsers = hydratedUsers
                topReviews = hydratedReviews
            } else {
                errorMessage = "Hubo un problema al cargar el salón de la fama."
            }

            isLoading = false
        }
    }
}