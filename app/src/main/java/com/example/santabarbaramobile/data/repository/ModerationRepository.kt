package com.example.santabarbaramobile.data.repository

import com.example.santabarbaramobile.data.model.AdminReportResponse
import com.example.santabarbaramobile.data.model.AdminActionRequestDto
import com.example.santabarbaramobile.data.remote.moderation.ModerationApi
import com.example.santabarbaramobile.data.remote.moderation.ReportRequestDto
import com.example.santabarbaramobile.data.security.TokenManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ModerationRepository @Inject constructor(
    private val api: ModerationApi,
    private val tokenManager: TokenManager
) {
    suspend fun createReport(targetType: String, targetId: String, reason: String, description: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${tokenManager.getToken()}"
            val request = ReportRequestDto(targetType, targetId, reason, description)
            val res = api.createReport(token, request)

            if (res.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("Error al enviar reporte. (Código: ${res.code()})"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAdminReports(status: String, page: Int): Result<AdminReportResponse> = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${tokenManager.getToken()}"
            val res = api.getAdminReports(token, status, page)

            if (res.isSuccessful && res.body()?.data != null) {
                Result.success(res.body()!!.data!!)
            } else {
                Result.failure(Exception("Error al cargar la bandeja de reportes (${res.code()})"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun executeReportAction(reportId: String, actionType: String, note: String, duration: String?): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${tokenManager.getToken()}"
            val request = AdminActionRequestDto(actionType, note, duration)
            val res = api.executeReportAction(token, reportId, request)

            if (res.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("Error al aplicar castigo. Código: ${res.code()}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun dismissReportDirectly(reportId: String, note: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${tokenManager.getToken()}"
            val res = api.dismissReport(token, reportId, mapOf("note" to note))

            if (res.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("Error al descartar reporte. Código: ${res.code()}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}