package com.example.santabarbaramobile.feature.shows.domain

import com.example.santabarbaramobile.core.network.SantaBarbaraApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ShowRepository @Inject constructor(
    private val api: SantaBarbaraApi
) {
    suspend fun getHome(): Result<HomeResponse> = withContext(Dispatchers.IO) {
        try {
            val response = api.getHome()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al cargar el catálogo de series"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getShowDetails(showId: String): Result<Show> = withContext(Dispatchers.IO) {
        try {
            val response = api.getShowById(showId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al cargar los detalles de la serie"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchShows(query: String): Result<List<Show>> = withContext(Dispatchers.IO) {
        try {
            val response = api.searchShows(query)
            if (response.isSuccessful && response.body() != null) {
                val showsUnpacked = response.body()!!.map { it.show }
                Result.success(showsUnpacked)
            } else {
                Result.failure(Exception("Error de servidor: Código ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}