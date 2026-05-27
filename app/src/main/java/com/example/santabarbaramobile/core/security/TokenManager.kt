package com.example.santabarbaramobile.core.security

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton
import android.util.Base64

@Singleton
class TokenManager @Inject constructor(@ApplicationContext context: Context) {

    private val prefs: SharedPreferences

    private val _isEmailVerified = MutableStateFlow(false)
    val isEmailVerified: StateFlow<Boolean> = _isEmailVerified.asStateFlow()

    init {
        prefs = try {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            EncryptedSharedPreferences.create(
                context,
                "secure_auth_prefs",
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            Log.e("TokenManager", "Fallo el Keystore. Usando Fallback de SharedPreferences.", e)
            context.getSharedPreferences("fallback_auth_prefs", Context.MODE_PRIVATE)
        }
        val savedEmail = prefs.getString("USER_EMAIL", "")
        if (!savedEmail.isNullOrBlank()) {
            _isEmailVerified.value = prefs.getBoolean("VERIFIED_$savedEmail", false)
        }
    }

    fun saveSession(token: String, email: String) {
        prefs.edit()
            .putString("JWT_TOKEN", token)
            .putString("USER_EMAIL", email)
            .apply()
        try {
            val parts = token.split(".")
            if (parts.size == 3) {
                val payload = String(android.util.Base64.decode(parts[1], android.util.Base64.URL_SAFE))
                val json = org.json.JSONObject(payload)
                val isVerified = json.optBoolean("emailVerified", false)
                updateVerificationStatus(email, isVerified)
            }
        } catch (e: Exception) {
            updateVerificationStatus(email, false)
        }
    }

    fun updateVerificationStatus(email: String, serverStatus: Boolean) {
        prefs.edit().putBoolean("VERIFIED_$email", serverStatus).apply()
        _isEmailVerified.value = serverStatus
    }

    fun markAsVerifiedLocally() {
        val savedEmail = prefs.getString("USER_EMAIL", "")
        if (!savedEmail.isNullOrBlank()) {
            prefs.edit().putBoolean("VERIFIED_$savedEmail", true).apply()
            _isEmailVerified.value = true
            Log.d("TokenManager", "Cuenta verificada con éxito localmente para: $savedEmail")
        }
    }

    fun getToken(): String? = prefs.getString("JWT_TOKEN", null)

    fun getUserEmail(): String? = prefs.getString("USER_EMAIL", null)

    fun clearToken() {
        prefs.edit().remove("JWT_TOKEN").remove("USER_EMAIL").apply()
        _isEmailVerified.value = false
        Log.d("TokenManager", "Sesión limpiada localmente.")
    }

    sealed class AuthEvent {
        object SessionExpired : AuthEvent()
    }

    private val _authEvent = kotlinx.coroutines.flow.MutableSharedFlow<AuthEvent>(
        extraBufferCapacity = 1,
        onBufferOverflow = kotlinx.coroutines.channels.BufferOverflow.DROP_OLDEST
    )
    val authEvent = _authEvent.asSharedFlow()

    fun triggerSessionExpired() {
        clearToken()
        _authEvent.tryEmit(AuthEvent.SessionExpired)
    }
}