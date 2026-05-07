package com.example.santabarbaramobile.data.repository

import com.example.santabarbaramobile.data.remote.auth.ForgotPasswordRequest
import com.example.santabarbaramobile.data.remote.auth.ForgotPasswordResponse
import com.example.santabarbaramobile.data.remote.auth.LoginRequest
import com.example.santabarbaramobile.data.remote.auth.LoginResponse
import com.example.santabarbaramobile.data.remote.auth.RegisterRequest
import com.example.santabarbaramobile.data.remote.auth.RegisterResponse
import com.example.santabarbaramobile.data.remote.auth.UserResponse
import com.example.santabarbaramobile.data.remote.auth.AuthApi
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
                Result.failure(Exception("Credenciales invalidas. Verifica tu correo y contrasena."))
            }
        } catch (e: HttpException) {
            Result.failure(Exception("Error de servidor: ${e.message()}"))
        } catch (e: IOException) {
            Result.failure(Exception("Sin conexion con el servidor. Verifica que Docker este corriendo."))
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
                Result.failure(Exception("No se pudo crear la cuenta. Revisa si el email o username ya existen."))
            }
        } catch (e: HttpException) {
            Result.failure(Exception("Error de servidor: ${e.message()}"))
        } catch (e: IOException) {
            Result.failure(Exception("Sin conexion con el servidor. Verifica que Docker este corriendo."))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun forgotPassword(email: String): Result<ForgotPasswordResponse> = withContext(Dispatchers.IO) {
        try {
            val response = authApi.forgotPassword(ForgotPasswordRequest(email))
            val body = response.body()

            if (response.isSuccessful && body != null) {
                Result.success(body)
            } else {
                Result.failure(Exception("No se pudo solicitar la recuperacion de contrasena."))
            }
        } catch (e: HttpException) {
            Result.failure(Exception("Error de servidor: ${e.message()}"))
        } catch (e: IOException) {
            Result.failure(Exception("Sin conexion con el servidor. Verifica que Docker este corriendo."))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserProfile(token: String): Result<UserResponse> = withContext(Dispatchers.IO) {
        try {
            val response = usersApi.getProfile(token)
            Result.success(response)
        } catch (e: HttpException) {
            Result.failure(Exception("Sesion expirada o token invalido"))
        } catch (e: IOException) {
            Result.failure(Exception("Sin conexion a internet."))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
