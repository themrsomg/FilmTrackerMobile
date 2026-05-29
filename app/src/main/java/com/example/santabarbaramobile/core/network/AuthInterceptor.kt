package com.example.santabarbaramobile.core.network

import com.example.santabarbaramobile.core.security.TokenManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()
        val token = tokenManager.getToken()

        if (!token.isNullOrEmpty()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        val response = chain.proceed(requestBuilder.build())
        val path = originalRequest.url.encodedPath
        val isAuthRoute = path.contains("/auth/login") || path.contains("/auth/register")

        if (response.code == 401 && !isAuthRoute) {
            tokenManager.triggerSessionExpired()
        }

        return response
    }
}