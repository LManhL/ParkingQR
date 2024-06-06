package com.example.parkingqr.data.remote.dto.payment

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CreateTokenResponseDTO(
    @Json(name = "url")
    val url: String,
)