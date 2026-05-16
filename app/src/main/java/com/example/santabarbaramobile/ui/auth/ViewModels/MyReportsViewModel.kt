package com.example.santabarbaramobile.ui.auth.ViewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.santabarbaramobile.data.model.AdminReportDto
import com.example.santabarbaramobile.data.repository.ModerationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyReportsViewModel @Inject constructor(
    private val moderationRepository: ModerationRepository
) : ViewModel() {

    var isLoading by mutableStateOf(true)
        private set
    var reports by mutableStateOf<List<AdminReportDto>>(emptyList())
        private set
    var error by mutableStateOf<String?>(null)
        private set

    init {
        fetchMyReports()
    }

    fun fetchMyReports() {
        viewModelScope.launch {
            isLoading = true
            error = null
            moderationRepository.getMyReports(page = 1)
                .onSuccess { response ->
                    reports = response.reports ?: emptyList()
                }
                .onFailure {
                    error = it.message
                }
            isLoading = false
        }
    }
}