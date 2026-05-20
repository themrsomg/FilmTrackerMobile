package com.example.santabarbaramobile.feature.shows.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.santabarbaramobile.core.network.SantaBarbaraApi
import com.example.santabarbaramobile.core.security.TokenManager
import com.example.santabarbaramobile.feature.profile.data.UserLibraryApi
import com.example.santabarbaramobile.feature.profile.domain.LibraryRequest
import com.example.santabarbaramobile.feature.shows.domain.ShowDetailState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShowDetailViewModel @Inject constructor(
    private val api: SantaBarbaraApi,
    private val libraryRepository: UserLibraryApi,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ShowDetailState())
    val uiState = _uiState.asStateFlow()

    fun fetchFullShowDetails(showId: String) {
        if (_uiState.value.show?.tvmazeId?.toString() == showId) return

        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                val token = "Bearer ${tokenManager.getToken() ?: ""}"
                coroutineScope {
                    val showDeferred = async { api.getShowById(showId).body() }
                    val castDeferred = async { api.getShowCast(showId).body()?.cast ?: emptyList() }
                    val seasonsDeferred =
                        async { api.getShowSeasons(showId).body()?.seasons ?: emptyList() }
                    val episodesDeferred =
                        async { api.getShowEpisodes(showId).body()?.episodes ?: emptyList() }

                    val isFavoriteDeferred = async {
                        try {
                            val response =
                                libraryRepository.getFavorites(token, page = 1, limit = 49)

                            if (response.isSuccessful) {
                                val favoritesList = response.body()?.data ?: emptyList()
                                favoritesList.any { it.tvmaze_id.toString() == showId }
                            } else {
                                false
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            false
                        }
                    }

                    val isInWatchlistDeferred = async {
                        try {
                            libraryRepository.checkIsInWatchlist(token, showId).body()?.exists
                                ?: false
                        } catch (e: Exception) {
                            false
                        }
                    }

                    val show = showDeferred.await()

                    val similarShowsDeferred = async {
                        val firstGenre = show?.genres?.firstOrNull()
                        if (firstGenre != null) {
                            api.getShowsByGenre(firstGenre).body()?.results ?: emptyList()
                        } else {
                            emptyList()
                        }
                    }

                    val cast = castDeferred.await()
                    val seasons = seasonsDeferred.await().sortedBy { it.number }
                    val allEpisodes = episodesDeferred.await()
                    val isFavorite = isFavoriteDeferred.await()
                    val isInWatchlist = isInWatchlistDeferred.await()

                    val similarShows = similarShowsDeferred.await()
                        .filter { it.tvmazeId.toString() != showId }

                    val groupedEpisodes = seasons.associateWith { season ->
                        allEpisodes.filter { it.season == season.number }.sortedBy { it.number }
                    }

                    if (show != null) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                show = show,
                                cast = cast,
                                seasonsWithEpisodes = groupedEpisodes,
                                similarShows = similarShows,
                                isFavorite = isFavorite,
                                isInWatchlist = isInWatchlist
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(isLoading = false, error = "Serie no encontrada")
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Error de red") }
            }
        }
    }

    fun toggleFavorite(showId: String) {
        val currentState = _uiState.value
        val currentlyFavorite = currentState.isFavorite
        val token = "Bearer ${tokenManager.getToken() ?: ""}"

        _uiState.update { it.copy(isFavorite = !currentlyFavorite, error = null) }

        viewModelScope.launch {
            try {
                val response = if (currentlyFavorite) {
                    libraryRepository.removeFavorite(token, showId)
                } else {
                    val tvMazeIdInt = showId.toIntOrNull() ?: throw Exception("ID inválido")
                    libraryRepository.addFavorite(token, LibraryRequest(tvMazeIdInt))
                }

                if (!response.isSuccessful && response.code() != 409) {
                    throw Exception("Error del servidor: ${response.code()}")
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isFavorite = currentlyFavorite,
                        error = "No se pudo actualizar favoritos. Intenta de nuevo."
                    )
                }
            }
        }
    }

    fun toggleWatchlist(showId: String) {
        val currentState = _uiState.value
        val currentlyInWatchlist = currentState.isInWatchlist
        val token = "Bearer ${tokenManager.getToken() ?: ""}"

        _uiState.update { it.copy(isInWatchlist = !currentlyInWatchlist, error = null) }

        viewModelScope.launch {
            try {
                val response = if (currentlyInWatchlist) {
                    libraryRepository.removeFromWatchlist(token, showId)
                } else {
                    val tvMazeIdInt = showId.toIntOrNull() ?: throw Exception("ID inválido")
                    libraryRepository.addToWatchlist(token, LibraryRequest(tvMazeIdInt))
                }

                if (!response.isSuccessful) throw Exception("Error: ${response.code()}")
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isInWatchlist = currentlyInWatchlist, error = "No se pudo actualizar la watchlist.")
                }
            }
        }
    }
}