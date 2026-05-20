package com.example.santabarbaramobile.core.security

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

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

        val matchesLocalVerification = prefs.getBoolean("VERIFIED_$email", false)
        _isEmailVerified.value = matchesLocalVerification
        Log.d("TokenManager", "Sesión guardada para $email. Estado de verificación local: $matchesLocalVerification")
    }

    fun updateVerificationStatus(email: String, serverStatus: Boolean) {
        val matchesLocalVerification = prefs.getBoolean("VERIFIED_$email", false)
        _isEmailVerified.value = matchesLocalVerification
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
}