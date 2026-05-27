package com.example.santabarbaramobile.core.network

import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

object NetworkErrorHandler {
    fun getFriendlyMessage(throwable: Throwable): String {

        if (throwable is HttpException) {
            try {
                val errorBody = throwable.response()?.errorBody()?.string()
                if (!errorBody.isNullOrEmpty()) {
                    val json = JSONObject(errorBody)
                    if (json.has("message")) {
                        return json.getString("message")
                    }
                }
            } catch (e: Exception) {
            }
            return when (throwable.code()) {
                401, 403 -> "Credenciales incorrectas o sesión expirada."
                404 -> "No pudimos encontrar la información solicitada."
                409 -> "Esta acción ya fue procesada o el registro ya existe."
                in 500..599 -> "Uno de nuestros servicios está en mantenimiento. Intenta en unos minutos."
                else -> "Algo salió mal. Intenta de nuevo más tarde."
            }
        }
        if (throwable is IOException) {
            return "El servidor está temporalmente fuera de servicio o no hay conexión a internet."
        }
        val msg = throwable.message ?: ""
        val lowerMsg = msg.lowercase()

        return when {
            lowerMsg.contains("failed to connect") || lowerMsg.contains("timeout") || lowerMsg.contains("socket") ->
                "El servidor está temporalmente fuera de servicio. Intenta más tarde."
            lowerMsg.contains("500") || lowerMsg.contains("502") || lowerMsg.contains("503") ->
                "Uno de nuestros servicios está en mantenimiento. Intenta en unos minutos."
            msg.isNotBlank() && !lowerMsg.contains("exception") && !lowerMsg.contains("retrofit") ->
                msg
            else -> "Algo salió mal, intenta de nuevo más tarde."
        }
    }
}