package com.example.santabarbaramobile.data.repository

import com.example.santabarbaramobile.data.remote.auth.*
import com.example.santabarbaramobile.data.remote.users.UsersApi
import com.example.santabarbaramobile.data.security.TokenManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val authApi: AuthApi,
    private val tokenManager: TokenManager,
    private val usersApi: UsersApi
) {

    suspend fun login(request: LoginRequest): Result<LoginResponse> = withContext(Dispatchers.IO) {
        try {
            val response = authApi.login(request)
            val body = response.body()

            if (response.isSuccessful && body != null) {
                tokenManager.saveToken(body.token)
                Result.success(body)
            } else {
                Result.failure(Exception("Error ${response.code()}: Credenciales inválidas"))
            }
        } catch (e: HttpException) {
            Result.failure(Exception("Error de servidor: ${e.message()}"))
        } catch (e: IOException) {
            Result.failure(Exception("Sin conexión a internet. Por favor, verifica tu red."))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(request: RegisterRequest): Result<RegisterResponse> = withContext(Dispatchers.IO) {
        try {
            val response = authApi.register(request)
            val body = response.body()

            if (response.isSuccessful && body != null) {
                Result.success(body)
            } else {
                Result.failure(Exception("Error ${response.code()}: El usuario ya existe o los datos son inválidos"))
            }
        } catch (e: HttpException) {
            Result.failure(Exception("Error de servidor: ${e.message()}"))
        } catch (e: IOException) {
            Result.failure(Exception("Sin conexión a internet. Por favor, verifica tu red."))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserProfile(token: String): Result<UserResponse> = withContext(Dispatchers.IO) {
        try {
            val response = usersApi.getProfile(token)
            Result.success(response)
        } catch (e: HttpException) {
            Result.failure(Exception("Sesión expirada o token inválido"))
        } catch (e: IOException) {
            Result.failure(Exception("Sin conexión a internet."))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}