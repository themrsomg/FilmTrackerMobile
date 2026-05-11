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

    fun loadUserProfile(userId: String? = null, username: String? = null) {
        viewModelScope.launch {
            val isOwnProfile = (userId == null)

            uiState = uiState.copy(
                isLoading = true,
                isOwnProfile = isOwnProfile,
                error = null
            )

            val token = tokenManager.getToken()

            if (token != null) {
                val bearerToken = "Bearer $token"

                val watchlistDeferred = if (isOwnProfile) {
                    async { libraryRepository.getMyWatchlist() }
                } else null

                val favoritesDeferred = if (isOwnProfile) {
                    async { libraryRepository.getMyFavorites() }
                } else {
                    async { libraryRepository.getUserFavorites(userId!!) }
                }

                var fetchedName = ""
                var fetchedUsername = ""
                var fetchedEmail = ""
                var fetchedProfileImage: String? = null
                var fetchedIsVerified = false
                var profileError: String? = null

                if (isOwnProfile) {
                    userRepository.getUserProfile(bearerToken)
                        .onSuccess {
                            fetchedName = it.name.orEmpty()
                            fetchedUsername = it.username
                            fetchedEmail = it.email ?: ""
                            fetchedProfileImage = it.profileImage
                            fetchedIsVerified = it.isEmailVerified
                        }
                        .onFailure { profileError = it.message }
                } else {
                    userRepository.searchUserByUsername(username ?: "")
                        .onSuccess {
                            fetchedName = it.name.orEmpty()
                            fetchedUsername = it.username
                            fetchedEmail = it.email ?: ""
                            fetchedProfileImage = it.profileImage
                            fetchedIsVerified = false
                        }
                        .onFailure { profileError = it.message }
                }

                val watchlistResult = watchlistDeferred?.await()
                val favoritesResult = favoritesDeferred.await()

                val rawWatchlist = watchlistResult?.getOrDefault(emptyList()) ?: emptyList()
                val rawFavorites = favoritesResult.getOrDefault(emptyList())

                val populatedWatchlist = rawWatchlist.map { item ->
                    async { hydrateItem(item) }
                }.awaitAll()

                val populatedFavorites = rawFavorites.map { item ->
                    async { hydrateItem(item) }
                }.awaitAll()

                if (profileError == null) {
                    uiState = uiState.copy(
                        name = fetchedName,
                        username = fetchedUsername,
                        email = fetchedEmail,
                        profileImage = fetchedProfileImage,
                        isEmailVerified = fetchedIsVerified,
                        watchlist = populatedWatchlist,
                        favorites = populatedFavorites,
                        isLoading = false
                    )
                } else {
                    uiState = uiState.copy(
                        error = profileError,
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