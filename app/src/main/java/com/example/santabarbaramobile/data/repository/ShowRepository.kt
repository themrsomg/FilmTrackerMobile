package com.example.santabarbaramobile.data.repository
/*
import com.example.santabarbaramobile.data.remote.SantaBarbaraApi
import com.example.santabarbaramobile.data.model.Show
import javax.inject.Inject

class ShowRepository @Inject constructor(
    private val api: SantaBarbaraApi
) {
    suspend fun getHomeData(): Result<List<Show>> {
        return try {
            val response = api.getPopularContent()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
 */