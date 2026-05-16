package com.example.santabarbaramobile.data.repository

import com.example.santabarbaramobile.data.model.dtos.UpdateProfileRequest
import com.example.santabarbaramobile.data.model.dtos.UserDto
import com.example.santabarbaramobile.data.model.models.UserResponse
import com.example.santabarbaramobile.data.remote.users.UsersApi
import com.example.santabarbaramobile.data.security.TokenManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val usersApi: UsersApi,
    private val tokenManager: TokenManager
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

    suspend fun searchUserByUsername(username: String): Result<UserDto> = withContext(Dispatchers.IO) {
        try {
            val formattedUsername = username.trim().replace(" ", "_")

            val response = usersApi.getUserByUsername(formattedUsername)

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.data != null) {
                    Result.success(body.data)
                } else {
                    Result.failure(Exception("Datos del usuario vacíos"))
                }
            } else {
                Result.failure(Exception("Usuario no encontrado"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserById(authId: String): Result<UserDto> = withContext(Dispatchers.IO) {
        try {
            val response = usersApi.getUserById(authId)
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data)
            } else {
                Result.failure(Exception("Usuario no encontrado"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProfile(name: String, bio: String?): Result<UserDto> = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${tokenManager.getToken()}"
            val request = UpdateProfileRequest(name, bio)
            val res = usersApi.updateProfile(token, request)

            if (res.isSuccessful && res.body() != null) {
                Result.success(res.body()!!)
            } else {
                Result.failure(Exception("Error al actualizar perfil. (Código: ${res.code()})"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun uploadProfilePhoto(photoPart: okhttp3.MultipartBody.Part): Result<UserDto> = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${tokenManager.getToken()}"
            val res = usersApi.uploadProfilePhoto(token, photoPart)

            if (res.isSuccessful && res.body() != null) {
                Result.success(res.body()!!)
            } else {
                Result.failure(Exception("Error al subir foto de perfil. (Código: ${res.code()})"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
