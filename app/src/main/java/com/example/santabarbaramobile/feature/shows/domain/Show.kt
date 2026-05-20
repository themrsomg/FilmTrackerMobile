package com.example.santabarbaramobile.feature.shows.domain

data class HomeResponse(
    val featured: List<Show> = emptyList(),
    val topRated: List<Show> = emptyList(),
    val recent: List<Show> = emptyList(),
    val ended: List<Show> = emptyList()
)

data class Show(
    val tvmazeId: Int,
    val name: String,
    val summary: String?,
    val image: ImageResponse?,
    val rating: RatingResponse?,
    val genres: List<String> = emptyList()
)

data class ImageResponse(
    val medium: String?,
    val original: String?
)

data class RatingResponse(
    val average: Double?
)

data class GenreResponse(
    val genre: String,
    val results: List<Show>
)

data class SearchResultItem(
    val score: Double,
    val show: Show
)