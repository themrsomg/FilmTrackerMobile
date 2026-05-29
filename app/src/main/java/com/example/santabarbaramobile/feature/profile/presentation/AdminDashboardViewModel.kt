package com.example.santabarbaramobile.feature.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.santabarbaramobile.feature.friends.domain.AdminDashboardRepository
import com.example.santabarbaramobile.feature.profile.domain.*
import com.example.santabarbaramobile.feature.reviews.domain.ModerationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminDashboardViewModel @Inject constructor(
    private val adminRepository: AdminDashboardRepository,
    private val moderationRepository: ModerationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminDashboardState())
    val uiState: StateFlow<AdminDashboardState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        loadDashboardStats()
        loadReports(_uiState.value.currentFilter)
        setupUserSearch()
    }

    @OptIn(FlowPreview::class)
    private fun setupUserSearch() {
        viewModelScope.launch {
            _searchQuery
                .debounce(500L)
                .filter { it.length >= 3 }
                .distinctUntilChanged()
                .collectLatest { query -> executeSearch(query) }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        if (query.isEmpty()) _uiState.update { it.copy(searchResults = emptyList()) }
    }

    private suspend fun executeSearch(query: String) {
        _uiState.update { it.copy(isSearching = true) }
        adminRepository.searchUsers(query).onSuccess { users ->
            _uiState.update { it.copy(searchResults = users, isSearching = false) }
        }.onFailure {
            _uiState.update { it.copy(isSearching = false, errorMessage = "Error en búsqueda") }
        }
    }

    fun loadUserDetails(authId: String) {
        if (_uiState.value.userDetailsMap.containsKey(authId)) return
        viewModelScope.launch {
            val detailsReq = async { adminRepository.getAdminUserDetails(authId) }
            val statusReq = async { adminRepository.getAccountStatus(authId) }

            val details = detailsReq.await().getOrNull()
            val status = statusReq.await().getOrNull()

            _uiState.update { state ->
                val newMap = state.userDetailsMap.toMutableMap()
                newMap[authId] = UserDetailData(details, status)
                state.copy(userDetailsMap = newMap)
            }
        }
    }

    fun loadDashboardStats() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val authDeferred = async { adminRepository.getAuthStats() }
            val reviewDeferred = async { adminRepository.getReviewStats() }
            val modDeferred = async { adminRepository.getModerationStats() }

            _uiState.update {
                it.copy(
                    authStats = authDeferred.await().getOrNull(),
                    reviewStats = reviewDeferred.await().getOrNull(),
                    modStats = modDeferred.await().getOrNull(),
                    isLoading = false
                )
            }
        }
    }

    fun loadReports(status: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isReportsLoading = true, currentFilter = status, errorMessage = null) }
            moderationRepository.getAdminReports(status, page = 1).onSuccess { response ->
                _uiState.update { it.copy(reportsList = response.reports ?: emptyList(), isReportsLoading = false) }
            }.onFailure {
                _uiState.update { it.copy(errorMessage = "Error al obtener reportes.", isReportsLoading = false) }
            }
        }
    }

    fun applyAction(reportId: String, actionType: String, note: String, duration: String? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isReportsLoading = true) }
            val result = if (actionType == "DISMISS_REPORT") {
                moderationRepository.dismissReportDirectly(reportId, note)
            } else {
                moderationRepository.executeReportAction(reportId, actionType, note, duration)
            }

            result.onSuccess {
                _uiState.update { it.copy(successMessage = "Acción ejecutada correctamente.", isReportsLoading = false) }
                loadReports(_uiState.value.currentFilter)
                loadDashboardStats()
            }.onFailure {
                _uiState.update { it.copy(errorMessage = "Fallo al aplicar acción.", isReportsLoading = false) }
            }
        }
    }

    fun executeDirectUserAction(authId: String, action: String, duration: String? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = when (action) {
                "BAN" -> adminRepository.banUser(authId, "Baneado administrativamente por App Móvil")
                "UNBAN" -> adminRepository.unbanUser(authId)
                "REMOVE_PHOTO" -> adminRepository.removeProfilePhoto(authId)
                "SUSPEND" -> adminRepository.suspendUser(authId, duration ?: "7_DAYS", "Suspensión desde panel móvil")
                else -> Result.success(Unit)
            }

            result.onSuccess {
                _uiState.update { state ->
                    val newMap = state.userDetailsMap.toMutableMap()
                    newMap.remove(authId)
                    state.copy(successMessage = "Acción directa aplicada.", isLoading = false, userDetailsMap = newMap)
                }
                loadUserDetails(authId)
            }.onFailure {
                _uiState.update { it.copy(errorMessage = "Error al aplicar acción.", isLoading = false) }
            }
        }
    }

    fun loadReporterUsername(authId: String) {
        if (_uiState.value.reporterUsernamesCache.containsKey(authId)) return
        viewModelScope.launch {
            adminRepository.getUserById(authId).onSuccess { user ->
                _uiState.update { state ->
                    state.copy(reporterUsernamesCache = state.reporterUsernamesCache + (authId to "@${user.username}"))
                }
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }
}