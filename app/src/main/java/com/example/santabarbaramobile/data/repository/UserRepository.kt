package com.example.santabarbaramobile.data.repository

import com.example.santabarbaramobile.data.remote.users.UsersApi
import com.example.santabarbaramobile.data.remote.auth.UserResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val usersApi: UsersApi
) {
    suspend fun getUserProfile(token: String): Result<UserResponse> = withContext(Dispatchers.IO) {
        try {
            val response = usersApi.getProfile(token)
            Result.success(response)
        } catch (e: HttpException) {
            Result.failure(Exception("Error HTTP: ${e.code()}"))
        } catch (e: IOException) {
            Result.failure(Exception("Falla de red o conexión cerrada por Docker: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}