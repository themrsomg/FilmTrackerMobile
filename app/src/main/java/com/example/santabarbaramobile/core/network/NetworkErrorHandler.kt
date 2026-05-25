package com.example.santabarbaramobile.core.network

import android.util.Log

object NetworkErrorHandler {
    fun isCritical(throwable: Throwable): Boolean {
        val errorMessage = throwable.message ?: ""
        return errorMessage.contains("401") || errorMessage.contains("403")
    }

    fun getFriendlyMessage(throwable: Throwable): String {
        return when {
            throwable.message?.contains("Failed to connect") == true ->
                "El servidor está temporalmente fuera de servicio. Intenta más tarde."
            throwable.message?.contains("404") == true ->
                "No pudimos encontrar la información solicitada."
            else -> "Algo salió mal. Por favor, intenta de nuevo."
        }
    }
}