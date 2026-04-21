package com.example.santabarbaramobile.ui.auth.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.santabarbaramobile.data.repository.ShowRepository
import com.example.santabarbaramobile.ui.auth.States.ShowDetailState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShowDetailViewModel @Inject constructor(
    private val showRepository: ShowRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ShowDetailState())
    val uiState = _uiState.asStateFlow()

    fun fetchShowDetails(showId: String) {
        if (_uiState.value.show?.tvmazeId?.toString() == showId) return
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            val result = showRepository.getShowDetails(showId)
            result.onSuccess { fetchedShow ->
                _uiState.update {
                    it.copy(isLoading = false, show = fetchedShow)
                }
            }.onFailure { exception ->
                _uiState.update {
                    it.copy(isLoading = false, error = exception.message ?: "Error al cargar la serie")
                }
            }
        }
    }
}