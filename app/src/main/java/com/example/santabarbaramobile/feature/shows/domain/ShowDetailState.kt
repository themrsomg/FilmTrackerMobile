package com.example.santabarbaramobile.feature.shows.domain

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