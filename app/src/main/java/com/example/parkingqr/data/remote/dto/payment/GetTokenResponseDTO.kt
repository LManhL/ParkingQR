package com.example.parkingqr.data.remote.dto.payment

import com.google.gson.annotations.SerializedName

data class GetTokenResponseDTO(

    val userId: String,

    val token: String
)