package com.example.santabarbaramobile.ui.auth.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.santabarbaramobile.data.remote.SantaBarbaraApi
import com.example.santabarbaramobile.ui.auth.States.ShowDetailState
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
    private val api: SantaBarbaraApi
) : ViewModel() {

    private val _uiState = MutableStateFlow(ShowDetailState())
    val uiState = _uiState.asStateFlow()

    fun fetchFullShowDetails(showId: String) {
        if (_uiState.value.show?.tvmazeId?.toString() == showId) return
        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                coroutineScope {
                    val showDeferred = async { api.getShowById(showId).body() }
                    val castDeferred = async { api.getShowCast(showId).body()?.cast ?: emptyList() }
                    val seasonsDeferred = async { api.getShowSeasons(showId).body()?.seasons ?: emptyList() }
                    val episodesDeferred = async { api.getShowEpisodes(showId).body()?.episodes ?: emptyList() }
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

                    val similarShows =
                        similarShowsDeferred.await().filter { it.tvmazeId.toString() != showId }

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
                                similarShows = similarShows
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = "Serie no encontrada"
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Error de red") }
            }
        }
    }
}