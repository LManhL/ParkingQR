package com.example.parkingqr.data.remote.dto.payment

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CreateTokenRequestDTO(
    @Json(name = "userId")
    val userId: String,
    @Json(name = "content")
    val content: String
)