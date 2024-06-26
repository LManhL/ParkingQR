package com.example.parkingqr.data.remote.payment

import com.example.parkingqr.data.remote.dto.payment.BankAccountResponseDTO
import com.example.parkingqr.data.remote.dto.payment.CreateTokenRequestDTO
import com.example.parkingqr.data.remote.dto.payment.CreateTokenResponseDTO
import com.example.parkingqr.data.remote.dto.payment.GetBankAccountListRequest
import com.example.parkingqr.data.remote.dto.payment.PayByTokenRequestDTO
import com.example.parkingqr.data.remote.dto.payment.PayByTokenResponseDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface PaymentService {
    @POST("/api/payment/create_token")
    suspend fun createToken(@Body body: CreateTokenRequestDTO): Response<ResponseObject<CreateTokenResponseDTO>>

    @POST("/api/payment/pay_by_token")
    suspend fun payByToken(@Body body: PayByTokenRequestDTO): Response<ResponseObject<PayByTokenResponseDTO>>

    @POST("/api/payment/bank_account_list")
    suspend fun getBankAccountList(@Body body: GetBankAccountListRequest): Response<ResponseObject<List<BankAccountResponseDTO>>>
}