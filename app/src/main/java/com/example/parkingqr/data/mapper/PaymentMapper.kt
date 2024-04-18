package com.example.parkingqr.data.mapper

import com.example.parkingqr.data.remote.payment.dto.BankAccountResponseDTO
import com.example.parkingqr.data.remote.payment.dto.CreateTokenResponseDTO
import com.example.parkingqr.data.remote.payment.dto.PayByTokenResponseDTO
import com.example.parkingqr.domain.model.payment.BankAccount
import com.example.parkingqr.domain.model.payment.CreateTokenResponse
import com.example.parkingqr.domain.model.payment.PayByTokenResponse

fun CreateTokenResponseDTO.mapToCreateTokenResponse(): CreateTokenResponse {
    return CreateTokenResponse(
        url = url
    )
}

fun BankAccountResponseDTO.mapToBankAccount(): BankAccount {
    return BankAccount(
        id = id,
        userId = userId,
        token = token,
        cardNumber = cardNumber,
        tmnCode = tmnCode,
        cardType = cardType,
        bankCode = bankCode,
        payDate = payDate,
    )
}

fun PayByTokenResponseDTO.mapToPayByTokenResponse(): PayByTokenResponse{
    return PayByTokenResponse(
        url = url
    )
}