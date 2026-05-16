package com.example.santabarbaramobile.data.model.dtos

data class PaginationDto(
    val page: Int?,
    val limit: Int?,
    val total: Int?,
    val totalPages: Int?,
    val hasNextPage: Boolean?,
    val hasPreviousPage: Boolean?
)