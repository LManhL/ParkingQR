package com.example.parkingqr.data.remote.payment.dto

import com.google.gson.annotations.SerializedName

data class GetTokenResponseDTO(

    val userId: String,

    val token: String
)