package com.example.santabarbaramobile.data.model.dtos

import com.google.gson.annotations.SerializedName

data class UserDto(
    @SerializedName("id", alternate = ["_id"]) val id: String? = null,
    @SerializedName("authId", alternate = ["auth_id"]) val authId: String? = null,
    val name: String? = null,
    val username: String,
    val email: String? = null,
    val profileImage: String? = null,
    val role: String? = "USER"
)

data class UserSearchResponse(
    @SerializedName("data") val data: UserDto
)