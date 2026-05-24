package com.example.santabarbaramobile.feature.profile.presentation

import android.util.Base64
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.santabarbaramobile.core.security.TokenManager
import com.example.santabarbaramobile.feature.auth.domain.AuthRepository
import com.example.santabarbaramobile.feature.profile.domain.UserRepository
import com.example.santabarbaramobile.feature.friends.domain.FriendsRepository
import com.example.santabarbaramobile.feature.profile.domain.LibraryRepository
import com.example.santabarbaramobile.feature.profile.domain.ProfileState
import com.example.santabarbaramobile.feature.reviews.domain.ModerationRepository
import com.example.santabarbaramobile.feature.shows.domain.LibraryItemDto
import com.example.santabarbaramobile.feature.shows.domain.ShowRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val libraryRepository: LibraryRepository,
    private val showRepository: ShowRepository,
    private val friendsRepository: FriendsRepository,
    private val authRepository: AuthRepository,
    private val moderationRepository: ModerationRepository,
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
                error = null,
                banSuccessMessage = null
            )

            val token = tokenManager.getToken()

            if (token != null) {
                val bearerToken = "Bearer $token"

                var roleFromToken = "USER"
                var myOwnAuthId = ""

                try {
                    val parts = token.split(".")
                    if (parts.size == 3) {
                        val payload = String(Base64.decode(parts[1], Base64.URL_SAFE))
                        val jsonObject = JSONObject(payload)
                        roleFromToken = jsonObject.optString("role", "USER")
                        myOwnAuthId = jsonObject.optString("authId", "")
                    }
                } catch (e: Exception) { /* Ignorar si falla el parseo */ }

                val watchlistDeferred = if (isOwnProfile) {
                    async { libraryRepository.getMyWatchlist() }
                } else null

                val favoritesDeferred = if (isOwnProfile) {
                    async { libraryRepository.getMyFavorites() }
                } else {
                    async { libraryRepository.getUserFavorites(userId!!) }
                }

                val statusDeferred = if (!isOwnProfile && userId != null) {
                    async { friendsRepository.getRelationshipStatus(userId) }
                } else null

                var fetchedName = ""
                var fetchedUsername = ""
                var fetchedEmail = ""
                var fetchedProfileImage: String? = null
                var profileError: String? = null

                if (isOwnProfile) {
                    userRepository.getUserProfile(bearerToken)
                        .onSuccess {
                            fetchedName = it.name.orEmpty()
                            fetchedUsername = it.username
                            fetchedEmail = it.email ?: ""
                            fetchedProfileImage = it.profileImage
                        }
                        .onFailure { profileError = it.message }
                } else {
                    userRepository.searchUserByUsername(username ?: "")
                        .onSuccess {
                            fetchedName = it.name.orEmpty()
                            fetchedUsername = it.username
                            fetchedEmail = it.email ?: ""
                            fetchedProfileImage = it.profileImage
                        }
                        .onFailure { profileError = it.message }
                }

                val watchlistResult = watchlistDeferred?.await()
                val favoritesResult = favoritesDeferred.await()
                val statusResult = statusDeferred?.await()

                val rawWatchlist = watchlistResult?.getOrDefault(emptyList()) ?: emptyList()
                val rawFavorites = favoritesResult.getOrDefault(emptyList())
                val fetchedStatus = statusResult?.getOrNull()?.status ?: "NONE"

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
                        isEmailVerified = if (isOwnProfile) tokenManager.isEmailVerified.value else false,
                        currentUserRole = roleFromToken,
                        watchlist = populatedWatchlist,
                        favorites = populatedFavorites,
                        targetAuthId = userId ?: myOwnAuthId,
                        friendshipStatus = fetchedStatus,
                        isLoading = false
                    )
                    if (!isOwnProfile && roleFromToken == "ADMIN" && userId != null) {
                        authRepository.getAccountStatus(userId).onSuccess { statusDto ->
                            uiState = uiState.copy(targetAccountStatus = statusDto.accountStatus ?: "ACTIVE")
                        }
                    }
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
            } else item
        } catch (e: Exception) { item }
    }

    fun suspendCurrentUser(days: Long) {
        val targetId = uiState.targetAuthId
        if (targetId.isEmpty()) return
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            authRepository.suspendUserDirectly(targetId, days, "Suspensión administrativa desde app móvil.")
                .onSuccess {
                    uiState = uiState.copy(isLoading = false, targetAccountStatus = "SUSPENDED", banSuccessMessage = "Usuario suspendido por $days días.")
                }
                .onFailure { uiState = uiState.copy(isLoading = false, error = "Error al suspender: ${it.message}") }
        }
    }

    fun banCurrentUser() {
        val targetId = uiState.targetAuthId
        if (targetId.isEmpty()) return
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            authRepository.banUserDirectly(targetId, "Violación a los términos (Admin)")
                .onSuccess {
                    uiState = uiState.copy(isLoading = false, targetAccountStatus = "BANNED", banSuccessMessage = "Usuario baneado permanentemente.")
                }
                .onFailure { uiState = uiState.copy(isLoading = false, error = "Error al banear: ${it.message}") }
        }
    }

    fun unbanCurrentUser() {
        val targetId = uiState.targetAuthId
        if (targetId.isEmpty()) return
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            authRepository.unbanUserDirectly(targetId)
                .onSuccess {
                    uiState = uiState.copy(isLoading = false, targetAccountStatus = "ACTIVE", banSuccessMessage = "Se ha restaurado el acceso al usuario.")
                }
                .onFailure { uiState = uiState.copy(isLoading = false, error = "Error al desbanear: ${it.message}") }
        }
    }

    fun sendFriendRequest() {
        val targetId = uiState.targetAuthId
        if (targetId.isEmpty()) return

        viewModelScope.launch {
            uiState = uiState.copy(friendshipStatus = "LOADING")
            friendsRepository.sendFriendRequest(targetId)
                .onSuccess {
                    uiState = uiState.copy(friendshipStatus = "PENDING_OUTGOING")
                }
                .onFailure {
                    uiState = uiState.copy(friendshipStatus = "NONE")
                }
        }
    }

    fun removeFriend() {
        val targetId = uiState.targetAuthId
        if (targetId.isEmpty()) return

        viewModelScope.launch {
            uiState = uiState.copy(friendshipStatus = "LOADING")
            friendsRepository.removeFriend(targetId)
                .onSuccess {
                    uiState = uiState.copy(friendshipStatus = "NONE")
                }
                .onFailure {
                    uiState = uiState.copy(friendshipStatus = "FRIENDS")
                }
        }
    }

    fun reportCurrentUser(reasonCode: String, description: String) {
        val targetId = uiState.targetAuthId
        if (targetId.isEmpty()) return

        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            moderationRepository.createReport("USER", targetId, reasonCode, description)
                .onSuccess {
                    uiState = uiState.copy(
                        isLoading = false,
                        banSuccessMessage = "Reporte enviado exitosamente al administrador."
                    )
                }
                .onFailure {
                    uiState = uiState.copy(
                        isLoading = false,
                        error = "Error al reportar: Si ya lo reportaste antes, debes esperar la revisión."
                    )
                }
        }
    }

    fun logout() {
        viewModelScope.launch {
            tokenManager.clearToken()
            uiState = ProfileState()
        }
    }
}