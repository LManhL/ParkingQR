package com.example.parkingqr.data.remote.payment.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CreateTokenRequestDTO(
    @Json(name = "userId")
    val userId: String,
    @Json(name = "content")
    val content: String
)