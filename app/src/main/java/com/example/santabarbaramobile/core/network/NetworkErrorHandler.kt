package com.example.santabarbaramobile.core.network

object NetworkErrorHandler {
    fun getFriendlyMessage(throwable: Throwable): String {
        val msg = throwable.message?.lowercase() ?: ""
        return when {
            msg.contains("failed to connect") || msg.contains("timeout") || msg.contains("socket") ->
                "El servidor está temporalmente fuera de servicio. Intenta más tarde."
            msg.contains("500") || msg.contains("502") || msg.contains("503") ->
                "Uno de nuestros servicios está en mantenimiento. Intenta en unos minutos."
            msg.contains("404") ->
                "No pudimos encontrar la información solicitada."
            msg.contains("409") ->
                "Esta acción ya fue procesada o el registro ya existe."
            msg.contains("401") || msg.contains("403") ->
                "Tu sesión ha expirado por seguridad."
            else -> "Algo salió mal, intenta de nuevo más tarde."
        }
    }
}