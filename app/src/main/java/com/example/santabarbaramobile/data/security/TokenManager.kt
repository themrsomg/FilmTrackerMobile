package com.example.santabarbaramobile.data.security

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor(@ApplicationContext context: Context) {

    private val prefs: SharedPreferences

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
    }

    fun saveToken(token: String) {
        prefs.edit().putString("JWT_TOKEN", token).apply()
        Log.d("TokenManager", "Token guardado en persistencia exitosamente.")
    }

    fun getToken(): String? {
        val token = prefs.getString("JWT_TOKEN", null)
        Log.d("TokenManager", "Token recuperado: ${if (token != null) "VÁLIDO" else "NULL"}")
        return token
    }

    fun clearToken() {
        prefs.edit().remove("JWT_TOKEN").apply()
        Log.d("TokenManager", "Sesión limpiada localmente.")
    }
}