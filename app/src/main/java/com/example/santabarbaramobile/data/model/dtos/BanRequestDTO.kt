package com.example.santabarbaramobile.data.model.dtos

data class SuspendRequestDto(
    val suspendedUntil: String,
    val reason: String
)

data class BanRequestDto(
    val reason: String
)

data class AccountStatusDto(
    val accountStatus: String?,
    val suspendedUntil: String?,
    val moderationReason: String?
)

data class AccountStatusResponse(
    val data: AccountStatusDto
)