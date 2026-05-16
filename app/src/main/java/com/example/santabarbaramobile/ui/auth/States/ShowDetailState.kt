package com.example.santabarbaramobile.ui.auth.States

import com.example.santabarbaramobile.data.model.models.CastMember
import com.example.santabarbaramobile.data.model.models.Episode
import com.example.santabarbaramobile.data.model.models.Season
import com.example.santabarbaramobile.data.model.models.Show

data class ShowDetailState(
    val isLoading: Boolean = true,
    val show: Show? = null,
    val cast: List<CastMember> = emptyList(),
    val seasonsWithEpisodes: Map<Season, List<Episode>> = emptyMap(),
    val similarShows: List<Show> = emptyList(),
    val isFavorite: Boolean = false,
    val isInWatchlist: Boolean = false,
    val error: String? = null
)