package com.example.santabarbaramobile.data.repository

import com.example.santabarbaramobile.data.model.HomeResponse
import com.example.santabarbaramobile.data.remote.SantaBarbaraApi
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ShowRepository @Inject constructor(
    private val api: SantaBarbaraApi
) {
    suspend fun getHomeData(): Result<HomeResponse> = withContext(Dispatchers.IO) {
        try {
            val response = api.getHomeData()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}