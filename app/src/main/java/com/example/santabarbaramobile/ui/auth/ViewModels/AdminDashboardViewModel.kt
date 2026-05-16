package com.example.santabarbaramobile.ui.auth.ViewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.santabarbaramobile.data.model.AuthStatsDto
import com.example.santabarbaramobile.data.model.ModerationStatsDto
import com.example.santabarbaramobile.data.model.ReviewStatsDto
import com.example.santabarbaramobile.data.repository.AdminDashboardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminDashboardViewModel @Inject constructor(
    private val repository: AdminDashboardRepository
) : ViewModel() {

    var isLoading by mutableStateOf(true)
        private set
    var authStats by mutableStateOf<AuthStatsDto?>(null)
        private set
    var reviewStats by mutableStateOf<ReviewStatsDto?>(null)
        private set
    var modStats by mutableStateOf<ModerationStatsDto?>(null)
        private set

    init {
        loadDashboardStats()
    }

    fun loadDashboardStats() {
        viewModelScope.launch {
            isLoading = true

            val authDeferred = async { repository.getAuthStats() }
            val reviewDeferred = async { repository.getReviewStats() }
            val modDeferred = async { repository.getModerationStats() }

            authStats = authDeferred.await().getOrNull()
            reviewStats = reviewDeferred.await().getOrNull()
            modStats = modDeferred.await().getOrNull()

            isLoading = false
        }
    }
}