package com.example.santabarbaramobile.feature.profile.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.santabarbaramobile.feature.shows.domain.Show
import com.example.santabarbaramobile.feature.profile.domain.UserDto
import com.example.santabarbaramobile.feature.shows.domain.ShowRepository
import com.example.santabarbaramobile.feature.profile.domain.UserRepository
import com.example.santabarbaramobile.core.security.TokenManager
import com.example.santabarbaramobile.feature.profile.domain.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject
import android.util.Base64
import com.example.santabarbaramobile.core.network.NetworkErrorHandler

@HiltViewModel
class MainHubViewModel @Inject constructor(
    private val showRepository: ShowRepository,
    private val userRepository: UserRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<Show>>(emptyList())
    val searchResults = _searchResults.asStateFlow()

    private val _searchUserResult = MutableStateFlow<UserDto?>(null)
    val searchUserResult = _searchUserResult.asStateFlow()

    private val _isSearchActive = MutableStateFlow(false)
    val isSearchActive = _isSearchActive.asStateFlow()

    var currentUserRole by mutableStateOf("USER")
        private set

    init {
        fetchHomeData()
        loadUserRole()
    }

    fun fetchHomeData() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            showRepository.getHome().onSuccess { homeData ->
                _uiState.value = HomeUiState.Success(homeData)
            }.onFailure { error ->
                val friendlyError = NetworkErrorHandler.getFriendlyMessage(error)
                _uiState.value = HomeUiState.Error(friendlyError)
            }
        }
    }

    private fun loadUserRole() {
        viewModelScope.launch {
            try {
                val token = tokenManager.getToken()
                if (token != null) {
                    val parts = token.split(".")
                    if (parts.size == 3) {
                        val flags = Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP
                        val payload = String(Base64.decode(parts[1], flags))
                        val jsonObject = JSONObject(payload)

                        currentUserRole = jsonObject.optString("role", "USER")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                currentUserRole = "USER"
            }
        }
    }

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