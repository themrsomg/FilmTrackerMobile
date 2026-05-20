package com.example.santabarbaramobile.feature.reviews.domain

data class PaginationDto(
    val page: Int?,
    val limit: Int?,
    val total: Int?,
    val totalPages: Int?,
    val hasNextPage: Boolean?,
    val hasPreviousPage: Boolean?
)