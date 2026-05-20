package com.example.santabarbaramobile.feature.profile.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.santabarbaramobile.feature.friends.domain.AdminDashboardRepository
import com.example.santabarbaramobile.feature.profile.domain.AdminReportDto
import com.example.santabarbaramobile.feature.profile.domain.AuthStatsDto
import com.example.santabarbaramobile.feature.profile.domain.ModerationStatsDto
import com.example.santabarbaramobile.feature.profile.domain.ReviewStatsDto
import com.example.santabarbaramobile.feature.reviews.domain.ModerationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminDashboardViewModel @Inject constructor(
    private val adminRepository: AdminDashboardRepository,
    private val moderationRepository: ModerationRepository
) : ViewModel() {

    var isLoading by mutableStateOf(true)
        private set
    var isReportsLoading by mutableStateOf(false)
        private set

    var authStats by mutableStateOf<AuthStatsDto?>(null)
        private set
    var reviewStats by mutableStateOf<ReviewStatsDto?>(null)
        private set
    var modStats by mutableStateOf<ModerationStatsDto?>(null)
        private set

    var reportsList by mutableStateOf<List<AdminReportDto>>(emptyList())
        private set
    var currentFilter by mutableStateOf("PENDING")
    var errorMessage by mutableStateOf<String?>(null)
        private set
    var successMessage by mutableStateOf<String?>(null)
        private set

    init {
        loadDashboardStats()
        loadReports(currentFilter)
    }

    fun loadDashboardStats() {
        viewModelScope.launch {
            isLoading = true
            val authDeferred = async { adminRepository.getAuthStats() }
            val reviewDeferred = async { adminRepository.getReviewStats() }
            val modDeferred = async { adminRepository.getModerationStats() }

            authStats = authDeferred.await().getOrNull()
            reviewStats = reviewDeferred.await().getOrNull()
            modStats = modDeferred.await().getOrNull()
            isLoading = false
        }
    }

    fun changeFilter(newFilter: String) {
        currentFilter = newFilter
        loadReports(newFilter)
    }

    fun loadReports(status: String) {
        viewModelScope.launch {
            isReportsLoading = true
            errorMessage = null
            moderationRepository.getAdminReports(status, page = 1)
                .onSuccess { response ->
                    reportsList = response.reports ?: emptyList()
                }
                .onFailure {
                    errorMessage = "Error al obtener reportes: ${it.message}"
                }
            isReportsLoading = false
        }
    }

    fun applyAction(reportId: String, actionType: String, note: String, duration: String? = null) {
        viewModelScope.launch {
            isReportsLoading = true
            val result = if (actionType == "DISMISS_REPORT") {
                moderationRepository.dismissReportDirectly(reportId, note)
            } else {
                moderationRepository.executeReportAction(reportId, actionType, note, duration)
            }

            result.onSuccess {
                successMessage = "Acción ejecutada correctamente."
                loadReports(currentFilter)
                loadDashboardStats()
            }
                .onFailure {
                    errorMessage = "Fallo al aplicar acción: ${it.message}"
                }
            isReportsLoading = false
        }
    }
}