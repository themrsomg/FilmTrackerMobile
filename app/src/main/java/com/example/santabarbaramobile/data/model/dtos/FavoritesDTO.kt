package com.example.santabarbaramobile.data.model.dtos

data class LibraryRequest(
    val tvmazeId: Int
)

data class CheckResponse(
    val exists: Boolean
)

data class FavoriteItem(
    val tvmaze_id: Int
)

data class FavoritesResponse(
    val data: List<FavoriteItem>
)