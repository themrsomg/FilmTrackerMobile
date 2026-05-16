package com.example.santabarbaramobile.data.repository

import com.example.santabarbaramobile.data.model.AdminActionRequestDto
import com.example.santabarbaramobile.data.model.ReportRequestDto
import com.example.santabarbaramobile.data.remote.moderation.ModerationApi
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
    suspend fun autoBanUser(targetAuthId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val token = "Bearer ${tokenManager.getToken()}"

            val reportReq = ReportRequestDto(
                targetType = "USER",
                targetId = targetAuthId,
                reason = "OTHER",
                description = "Auto-reporte para Baneo Manual desde Android"
            )
            val reportRes = api.createReport(token, reportReq)

            if (!reportRes.isSuccessful || reportRes.body()?.data == null) {
                return@withContext Result.failure(Exception("Error al crear el reporte inicial"))
            }

            val reportId = reportRes.body()!!.data.id

            val actionReq = AdminActionRequestDto(
                actionType = "BAN",
                note = "Baneado manualmente por el administrador desde la App Móvil"
            )
            val actionRes = api.executeAction(token, reportId, actionReq)

            if (actionRes.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al aplicar la suspensión"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}