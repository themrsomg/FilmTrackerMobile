package com.example.santabarbaramobile.feature.shows.domain

sealed class ResourceState<out T> {
    data class Success<T>(val data: T) : ResourceState<T>()
    data class Error(val message: String, val cause: Exception? = null) : ResourceState<Nothing>()
    object Loading : ResourceState<Nothing>()
}