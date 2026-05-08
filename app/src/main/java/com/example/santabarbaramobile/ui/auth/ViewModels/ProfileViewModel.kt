package com.example.santabarbaramobile.ui.auth.ViewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.santabarbaramobile.data.remote.library.LibraryItemDto
import com.example.santabarbaramobile.data.repository.LibraryRepository
import com.example.santabarbaramobile.data.repository.ShowRepository
import com.example.santabarbaramobile.data.repository.UserRepository
import com.example.santabarbaramobile.data.security.TokenManager
import com.example.santabarbaramobile.ui.auth.States.ProfileState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val libraryRepository: LibraryRepository,
    private val showRepository: ShowRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    var uiState by mutableStateOf(ProfileState())
        private set

    init {
        loadUserProfile(userId = null)
    }

    fun loadUserProfile(userId: String? = null) {
        viewModelScope.launch {
            uiState = uiState.copy(
                isLoading = true,
                isOwnProfile = (userId == null),
                error = null
            )

            val token = tokenManager.getToken()

            if (token != null) {
                val bearerToken = "Bearer $token"

                val profileDeferred = async { userRepository.getUserProfile(bearerToken) }
                val watchlistDeferred = async { libraryRepository.getWatchlist(userId) }
                val favoritesDeferred = if (userId == null) {
                    async { libraryRepository.getMyFavorites() }
                } else null

                val profileResult = profileDeferred.await()
                val watchlistResult = watchlistDeferred.await()
                val favoritesResult = favoritesDeferred?.await()
                val rawWatchlist = watchlistResult.getOrDefault(emptyList())
                val rawFavorites = favoritesResult?.getOrDefault(emptyList()) ?: emptyList()

                val populatedWatchlist = rawWatchlist.map { item ->
                    async { hydrateItem(item) }
                }.awaitAll()

                val populatedFavorites = rawFavorites.map { item ->
                    async { hydrateItem(item) }
                }.awaitAll()

                profileResult.onSuccess { userResponse ->
                    uiState = uiState.copy(
                        name = userResponse.name.orEmpty(),
                        username = userResponse.username,
                        email = userResponse.email,
                        profileImage = userResponse.profileImage,
                        isEmailVerified = userResponse.isEmailVerified,
                        watchlist = populatedWatchlist,
                        favorites = populatedFavorites,
                        isLoading = false
                    )
                }.onFailure { exception ->
                    uiState = uiState.copy(
                        error = exception.message ?: "Error desconocido al cargar perfil",
                        isLoading = false
                    )
                }
            } else {
                uiState = uiState.copy(isLoading = false, error = "Token no encontrado")
            }
        }
    }

    private suspend fun hydrateItem(item: LibraryItemDto): LibraryItemDto {
        return try {
            val showResult = showRepository.getShowDetails(item.tvmazeId.toString())

            if (showResult.isSuccess) {
                val show = showResult.getOrNull()
                item.copy(
                    name = show?.name ?: "Desconocido",
                    imageUrl = show?.image?.medium ?: show?.image?.original
                )
            } else {
                item
            }
        } catch (e: Exception) {
            item
        }
    }

    fun logout() {
        viewModelScope.launch {
            tokenManager.clearToken()
            uiState = ProfileState()
        }
    }
}