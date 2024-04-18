package com.example.parkingqr.data.remote.payment.dto

class BankAccountResponseDTO(
    val id: Long = 0,
    val userId: String = "",
    val token: String = "",
    val cardNumber: String = "",
    val tmnCode: String = "",
    val cardType: String = "",
    val bankCode: String = "",
    val payDate: String = "",
)