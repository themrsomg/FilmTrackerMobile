package com.example.santabarbaramobile.data.remote.moderation

import com.example.santabarbaramobile.data.model.AdminActionRequestDto
import com.example.santabarbaramobile.data.model.AdminStatsResponse
import com.example.santabarbaramobile.data.model.AdminReportResponse
import com.example.santabarbaramobile.data.model.ModerationStatsDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

data class ReportRequestDto(
    val targetType: String,
    val targetId: String,
    val reason: String,
    val description: String
)

interface ModerationApi {
    @POST("api/moderation/reports")
    suspend fun createReport(
        @Header("Authorization") token: String,
        @Body request: ReportRequestDto
    ): Response<Unit>

    @GET("api/moderation/admin/reports")
    suspend fun getAdminReports(
        @Header("Authorization") token: String,
        @Query("status") status: String,
        @Query("page") page: Int
    ): Response<AdminStatsResponse<AdminReportResponse>>

    @POST("api/moderation/admin/reports/{reportId}/actions")
    suspend fun executeReportAction(
        @Header("Authorization") token: String,
        @Path("reportId") reportId: String,
        @Body request: AdminActionRequestDto
    ): Response<Unit>

    @POST("api/moderation/admin/reports/{reportId}/dismiss")
    suspend fun dismissReport(
        @Header("Authorization") token: String,
        @Path("reportId") reportId: String,
        @Body request: Map<String, String>
    ): Response<Unit>

    @GET("api/moderation/admin/stats")
    suspend fun getModerationStats(@Header("Authorization") token: String):
            Response<AdminStatsResponse<ModerationStatsDto>>
}