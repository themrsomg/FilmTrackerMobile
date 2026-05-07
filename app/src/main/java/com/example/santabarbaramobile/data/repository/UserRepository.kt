package com.example.santabarbaramobile.data.repository

import com.example.santabarbaramobile.data.remote.auth.UserResponse
import com.example.santabarbaramobile.data.remote.users.UsersApi
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
            val body = response.body()?.data

            if (response.isSuccessful && body != null) {
                Result.success(body)
            } else {
                Result.failure(Exception("No se pudo cargar el perfil del usuario"))
            }
        } catch (e: HttpException) {
            Result.failure(Exception("Error HTTP: ${e.code()}"))
        } catch (e: IOException) {
            Result.failure(Exception("Falla de red o conexion cerrada por Docker: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
