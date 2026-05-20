package com.example.santabarbaramobile.feature.profile.domain

import com.example.santabarbaramobile.core.security.TokenManager
import com.example.santabarbaramobile.feature.shows.domain.LibraryItemDto
import com.example.santabarbaramobile.feature.profile.data.UserLibraryApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LibraryRepository @Inject constructor(
    private val api: UserLibraryApi,
    private val tokenManager: TokenManager
) {
    suspend fun getMyFavorites(): Result<List<LibraryItemDto>> = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${tokenManager.getToken()}"
            val response = api.getMyFavorites(token)
            if (response.isSuccessful) {
                Result.success(response.body()?.data ?: emptyList())
            } else {
                Result.failure(Exception("Error al cargar favoritos"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMyWatchlist(): Result<List<LibraryItemDto>> = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${tokenManager.getToken()}"
            val response = api.getMyWatchlist(token)

            if (response.isSuccessful) {
                Result.success(response.body()?.data ?: emptyList())
            } else {
                Result.failure(Exception("Error al cargar watchlist"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserFavorites(userId: String): Result<List<LibraryItemDto>> =
        withContext(Dispatchers.IO) {
            try {
                val token = "Bearer ${tokenManager.getToken()}"
                val response = api.getUserFavorites(token, userId)
                if (response.isSuccessful) {
                    Result.success(response.body()?.data ?: emptyList())
                } else {
                    Result.failure(Exception("Error al obtener favoritos del usuario"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}