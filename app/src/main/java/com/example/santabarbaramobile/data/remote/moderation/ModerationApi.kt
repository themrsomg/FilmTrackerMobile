package com.example.santabarbaramobile.data.remote.moderation

import com.example.santabarbaramobile.data.model.AdminActionRequestDto
import com.example.santabarbaramobile.data.model.AdminStatsResponse
import com.example.santabarbaramobile.data.model.ModerationStatsDto
import com.example.santabarbaramobile.data.model.ReportApiResponse
import com.example.santabarbaramobile.data.model.ReportRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ModerationApi {
    @POST("api/moderation/reports")
    suspend fun createReport(
        @Header("Authorization") token: String,
        @Body request: ReportRequestDto
    ): Response<ReportApiResponse>

    @POST("api/moderation/admin/reports/{reportId}/actions")
    suspend fun executeAction(
        @Header("Authorization") token: String,
        @Path("reportId") reportId: String,
        @Body request: AdminActionRequestDto
    ): Response<Unit>

    @GET("api/moderation/admin/stats")
    suspend fun getModerationStats(@Header("Authorization") token: String): Response<AdminStatsResponse<ModerationStatsDto>>
}