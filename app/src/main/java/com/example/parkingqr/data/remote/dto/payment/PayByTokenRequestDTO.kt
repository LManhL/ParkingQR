package com.example.parkingqr.data.remote.dto.payment


data class PayByTokenRequestDTO(

    val userId: String,

    val amount: Double,

    val content: String,

    val token: String
)