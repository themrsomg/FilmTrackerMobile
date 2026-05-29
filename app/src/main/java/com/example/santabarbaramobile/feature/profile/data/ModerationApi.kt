package com.example.santabarbaramobile.feature.profile.data

import com.example.santabarbaramobile.feature.profile.domain.AccountStatusDto
import com.example.santabarbaramobile.feature.profile.domain.AdminActionRequestDto
import com.example.santabarbaramobile.feature.profile.domain.AdminReportResponse
import com.example.santabarbaramobile.feature.profile.domain.AdminStatsResponse
import com.example.santabarbaramobile.feature.profile.domain.BanRequestDto
import com.example.santabarbaramobile.feature.profile.domain.ModerationStatsDto
import com.example.santabarbaramobile.feature.profile.domain.ReportRequestDto
import com.example.santabarbaramobile.feature.profile.domain.SuspendRequestDto
import com.example.santabarbaramobile.feature.profile.domain.UserDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

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

    @GET("api/moderation/reports/my")
    suspend fun getMyReports(
        @Header("Authorization") token: String,
        @Query("page") page: Int = 1
    ): Response<AdminStatsResponse<AdminReportResponse>>

    @GET("api/admin/users/search")
    suspend fun searchUsers(
        @Header("Authorization") token: String,
        @Query("q") query: String
    ): Response<List<UserDto>>

    @GET("api/admin/users/{authId}/status")
    suspend fun getAccountStatus(
        @Header("Authorization") token: String,
        @Path("authId") authId: String
    ): Response<AccountStatusDto>

    @PATCH("api/admin/users/{authId}/suspend")
    suspend fun suspendUser(
        @Header("Authorization") token: String,
        @Path("authId") authId: String,
        @Body request: SuspendRequestDto
    ): Response<Unit>

    @PATCH("api/admin/users/{authId}/ban")
    suspend fun banUser(
        @Header("Authorization") token: String,
        @Path("authId") authId: String,
        @Body request: BanRequestDto
    ): Response<Unit>

    @DELETE("api/admin/users/{authId}/profile-photo")
    suspend fun removeProfilePhotoDirectly(
        @Header("Authorization") token: String,
        @Path("authId") authId: String
    ): Response<Unit>
}