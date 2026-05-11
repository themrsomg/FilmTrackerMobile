package com.example.santabarbaramobile.ui.auth.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.santabarbaramobile.data.model.Show
import com.example.santabarbaramobile.data.model.UserDto // <-- Asegúrate de importar esto
import com.example.santabarbaramobile.data.repository.ShowRepository
import com.example.santabarbaramobile.data.repository.UserRepository // <-- Importamos tu repositorio
import com.example.santabarbaramobile.ui.auth.States.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainHubViewModel @Inject constructor(
    private val showRepository: ShowRepository,
    private val userRepository: UserRepository
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

    private val _searchUserResult = MutableStateFlow<UserDto?>(null)
    val searchUserResult = _searchUserResult.asStateFlow()

    private val _isSearchActive = MutableStateFlow(false)
    val isSearchActive = _isSearchActive.asStateFlow()

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
        if (newQuery.length > 2) {
            performSearch(newQuery)
        } else {
            _searchResults.value = emptyList()
            _searchUserResult.value = null
        }
    }

    fun setSearchActive(active: Boolean) {
        _isSearchActive.value = active
        if (!active) {
            _searchQuery.value = ""
            _searchUserResult.value = null
        }
    }

    private fun performSearch(query: String) {
        viewModelScope.launch {
            val showsDeferred = async { showRepository.searchShows(query) }

            val formattedUsername = query.trim().replace(" ", "_")
            val userDeferred = async { userRepository.searchUserByUsername(formattedUsername) }

            showsDeferred.await()
                .onSuccess { _searchResults.value = it }
                .onFailure { _searchResults.value = emptyList() }

            userDeferred.await()
                .onSuccess { _searchUserResult.value = it }
                .onFailure { _searchUserResult.value = null }
        }
    }
}