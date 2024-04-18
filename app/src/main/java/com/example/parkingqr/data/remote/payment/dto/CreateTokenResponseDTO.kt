package com.example.parkingqr.data.remote.payment.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CreateTokenResponseDTO(
    @Json(name = "url")
    val url: String,
)