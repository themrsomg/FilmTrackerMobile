package com.example.santabarbaramobile.data.repository

import com.example.santabarbaramobile.data.remote.auth.*
import com.example.santabarbaramobile.data.security.TokenManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val api: AuthApi,
    private val tokenManager: TokenManager
) {
    suspend fun login(request: LoginRequest): Result<LoginResponse> = withContext(Dispatchers.IO) {
        try {
            val response = api.login(request)
            if (response.isSuccessful && response.body() != null) {
                tokenManager.saveToken(response.body()!!.token)
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Credenciales inválidas o error de servidor"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(request: RegisterRequest): Result<RegisterResponse> = withContext(Dispatchers.IO) {
        try {
            val response = api.register(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("El usuario ya existe o los datos son inválidos"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}