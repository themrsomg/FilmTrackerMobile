package com.example.santabarbaramobile.data.model

import com.google.gson.annotations.SerializedName

data class ReportRequestDto(
    val targetType: String,
    val targetId: String,
    val reason: String,
    val description: String
)

data class AdminActionRequestDto(
    val actionType: String,
    val note: String,
    val duration: String? = null
)

data class ReportResponseDto(
    @SerializedName("_id", alternate = ["id"]) val id: String
)

data class ReportApiResponse(
    val data: ReportResponseDto
)

data class AdminReportDto(
    val id: Int,
    @SerializedName("reporter_auth_id") val reporterAuthId: String,
    @SerializedName("target_type") val targetType: String,
    @SerializedName("target_id") val targetId: String,
    val reason: String,
    val description: String,
    val status: String,
    @SerializedName("target_snapshot") val targetSnapshot: Map<String, Any>?,
    val availableActions: List<String>?,
    @SerializedName("created_at") val createdAt: String
)

data class AdminReportResponse(
    val reports: List<AdminReportDto>?,
    val pagination: PaginationDto?
)