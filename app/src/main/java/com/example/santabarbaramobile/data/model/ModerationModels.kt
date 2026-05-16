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