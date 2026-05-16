package com.example.santabarbaramobile.data.model.models

data class Season(
    val id: Int,
    val number: Int,
    val premiereDate: String?
)
data class Episode(
    val id: Int,
    val name: String,
    val season: Int,
    val number: Int,
    val summary: String?,
    val image: ShowImage?
)
data class CastMember(
    val person: Person,
    val character: Character
)
data class Person(
    val name: String,
    val image: ShowImage?
)
data class Character(
    val name: String
)
data class ShowImage(
    val medium: String?,
    val original: String?
)

data class SeasonResponse(
    val seasons: List<Season>
)

data class EpisodeResponse(
    val episodes: List<Episode>
)

data class CastResponse(
    val cast: List<CastMember>
)