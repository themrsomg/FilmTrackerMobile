package com.example.santabarbaramobile.feature.auth.domain

import com.example.santabarbaramobile.core.security.TokenManager
import com.example.santabarbaramobile.feature.profile.domain.AccountStatusDto
import com.example.santabarbaramobile.feature.profile.domain.BanRequestDto
import com.example.santabarbaramobile.feature.profile.domain.SuspendRequestDto
import com.example.santabarbaramobile.feature.profile.data.UsersApi
import com.example.santabarbaramobile.feature.auth.data.AuthApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
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
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                val errorMessage = extractErrorMessage(response.errorBody())
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión al servidor"))
        }
    }

    suspend fun register(request: RegisterRequest): Result<RegisterResponse> =
        withContext(Dispatchers.IO) {
            try {
                val response = authApi.register(request)
                val body = response.body()?.data

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

    suspend fun forgotPassword(email: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = authApi.forgotPassword(ForgotPasswordRequest(email))

            if (response.isSuccessful) {
                Result.success(Unit)
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

    suspend fun resetPassword(email: String, code: String, newPassword: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val res = authApi.resetPassword(ResetPasswordRequest(email, code, newPassword))
                if (res.isSuccessful) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Código inválido o expirado. (Error: ${res.code()})"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun changePassword(currentPassword: String, newPassword: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val token = "Bearer ${tokenManager.getToken()}"
                val request = ChangePasswordRequest(currentPassword, newPassword)
                val res = authApi.changePassword(token, request)

                if (res.isSuccessful) {
                    Result.success(Unit)
                } else {
                    val msg = if (res.code() == 400 || res.code() == 401) {
                        "La contraseña actual es incorrecta."
                    } else {
                        "Error al actualizar la contraseña (${res.code()})"
                    }
                    Result.failure(Exception(msg))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

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
            Result.failure(Exception("Sesion expirada o token invalido"))
        } catch (e: IOException) {
            Result.failure(Exception("Sin conexion a internet."))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun verifyEmail(email: String, code: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val request = VerifyEmailRequest(email, code)
                val response = authApi.verifyEmail(request)

                if (response.isSuccessful) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("El código es incorrecto o ha expirado"))
                }
            } catch (e: HttpException) {
                Result.failure(Exception("Error de servidor: ${e.message()}"))
            } catch (e: IOException) {
                Result.failure(Exception("Sin conexión con el servidor."))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    private fun extractErrorMessage(errorBody: ResponseBody?): String {
        return try {
            val errorString = errorBody?.string()
            val jsonObject = JSONObject(errorString ?: "")
            jsonObject.getString("message")
        } catch (e: Exception) {
            "Credenciales incorrectas o problema de servidor."
        }
    }

    suspend fun getAccountStatus(authId: String): Result<AccountStatusDto> =
        withContext(Dispatchers.IO) {
            try {
                val token = "Bearer ${tokenManager.getToken()}"
                val res = authApi.getAccountStatus(token, authId)
                if (res.isSuccessful && res.body()?.data != null) Result.success(res.body()!!.data)
                else Result.failure(Exception("Error HTTP ${res.code()}"))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun suspendUserDirectly(authId: String, days: Long, reason: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val token = "Bearer ${tokenManager.getToken()}"
                val futureDate = ZonedDateTime.now(ZoneOffset.UTC).plusDays(days)
                val suspendedUntilStr = futureDate.format(DateTimeFormatter.ISO_INSTANT)

                val request = SuspendRequestDto(suspendedUntilStr, reason)
                val res = authApi.suspendUser(token, authId, request)

                if (res.isSuccessful) Result.success(Unit) else Result.failure(Exception("Error ${res.code()}"))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun banUserDirectly(authId: String, reason: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val token = "Bearer ${tokenManager.getToken()}"
                val request = BanRequestDto(reason)
                val res = authApi.banUser(token, authId, request)

                if (res.isSuccessful) Result.success(Unit) else Result.failure(Exception("Error ${res.code()}"))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun unbanUserDirectly(authId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${tokenManager.getToken()}"
            val res = authApi.unbanUser(token, authId)

            if (res.isSuccessful) Result.success(Unit) else Result.failure(Exception("Error ${res.code()}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun resendVerification(email: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = authApi.resendVerification(ResendVerificationRequest(email))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("No se pudo reenviar el código."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}