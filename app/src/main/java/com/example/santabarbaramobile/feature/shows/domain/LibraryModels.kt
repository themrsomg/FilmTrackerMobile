package com.example.santabarbaramobile.feature.shows.domain

import com.google.gson.annotations.SerializedName

data class LibraryItemDto(
    @SerializedName("tvmaze_id") val tvmazeId: Int,
    val name: String? = null,
    val imageUrl: String? = null
)

data class LibraryListResponse(
    @SerializedName("data") val data: List<LibraryItemDto>
)