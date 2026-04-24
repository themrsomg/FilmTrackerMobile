package com.example.santabarbaramobile.ui.auth.States

import com.example.santabarbaramobile.data.model.CastMember
import com.example.santabarbaramobile.data.model.Episode
import com.example.santabarbaramobile.data.model.Season
import com.example.santabarbaramobile.data.model.Show

data class ShowDetailState(
    val isLoading: Boolean = true,
    val show: Show? = null,
    val cast: List<CastMember> = emptyList(),
    val seasonsWithEpisodes: Map<Season, List<Episode>> = emptyMap(),
    val similarShows: List<Show> = emptyList(),
    val error: String? = null
)