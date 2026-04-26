package com.example.santabarbaramobile.ui.auth.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.santabarbaramobile.data.model.Show
import com.example.santabarbaramobile.data.repository.ShowRepository
import com.example.santabarbaramobile.ui.auth.States.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainHubViewModel @Inject constructor(
    private val showRepository: ShowRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        fetchHomeData()
    }

    fun fetchHomeData() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            showRepository.getHome().onSuccess { homeData ->
                _uiState.value = HomeUiState.Success(homeData)
            }.onFailure { error ->
                _uiState.value = HomeUiState.Error(error.message ?: "Error al cargar catálogo")
            }
        }
    }

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<Show>>(emptyList())
    val searchResults = _searchResults.asStateFlow()

    private val _isSearchActive = MutableStateFlow(false)
    val isSearchActive = _isSearchActive.asStateFlow()

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
        if (newQuery.length > 2) {
            performSearch(newQuery)
        } else {
            _searchResults.value = emptyList()
        }
    }

    fun setSearchActive(active: Boolean) {
        _isSearchActive.value = active
        if (!active) _searchQuery.value = ""
    }

    private fun performSearch(query: String) {
        viewModelScope.launch {
            showRepository.searchShows(query)
                .onSuccess { resultados ->
                    _searchResults.value = resultados
                }
                .onFailure { error ->
                    error.printStackTrace()
                    _searchResults.value = emptyList()
                }
        }
    }
}