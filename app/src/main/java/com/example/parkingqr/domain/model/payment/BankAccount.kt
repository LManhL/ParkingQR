package com.example.parkingqr.domain.model.payment

class BankAccount(
    val id: Long = 0,
    val userId: String = "",
    val token: String = "",
    val cardNumber: String = "",
    val tmnCode: String = "",
    val cardType: String = "",
    val bankCode: String = "",
    val payDate: String = "",
)