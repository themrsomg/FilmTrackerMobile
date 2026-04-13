package com.example.santabarbaramobile.data.model

// El DTO (Data Transfer Object) que viaja entre Node.js, Java y Kotlin
data class Show(
    val id: Int,
    val name: String,
    val summary: String?,
    val imageUrl: String?,
    val category: String?
)