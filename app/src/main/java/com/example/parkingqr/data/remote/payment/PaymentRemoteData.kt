package com.example.parkingqr.data.remote.payment

import com.example.parkingqr.data.remote.payment.dto.BankAccountResponseDTO
import com.example.parkingqr.data.remote.payment.dto.CreateTokenResponseDTO
import com.example.parkingqr.data.remote.payment.dto.PayByTokenResponseDTO
import retrofit2.Response

interface PaymentRemoteData {
    suspend fun payByToken(amount: Double, token: String): Response<ResponseObject<PayByTokenResponseDTO>>
    suspend fun connectBankAccount(): Response<ResponseObject<CreateTokenResponseDTO>>
    suspend fun getBankAccountList(): Response<ResponseObject<List<BankAccountResponseDTO>>>
}