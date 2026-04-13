package com.example.santabarbaramobile.data.model

import com.google.gson.annotations.SerializedName

data class HomeResponse(
    val featured: List<Show>,
    val topRated: List<Show>,
    val recent: List<Show>,
    val ended: List<Show>
)

data class Show(
    val tvmazeId: Int,
    val name: String,
    val summary: String?,
    val image: ImageResponse?,
    val rating: RatingResponse?
)

data class ImageResponse(
    val medium: String?,
    val original: String?
)

data class RatingResponse(
    val average: Double?
)