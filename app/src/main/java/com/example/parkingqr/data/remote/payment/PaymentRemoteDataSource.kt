package com.example.parkingqr.data.remote.payment

import android.content.Context
import com.example.parkingqr.data.remote.BaseRemoteDataSource
import com.example.parkingqr.data.remote.ServiceGenerator
import com.example.parkingqr.data.remote.dto.payment.BankAccountResponseDTO
import com.example.parkingqr.data.remote.dto.payment.CreateTokenRequestDTO
import com.example.parkingqr.data.remote.dto.payment.CreateTokenResponseDTO
import com.example.parkingqr.data.remote.dto.payment.GetBankAccountListRequest
import com.example.parkingqr.data.remote.dto.payment.PayByTokenRequestDTO
import com.example.parkingqr.data.remote.dto.payment.PayByTokenResponseDTO
import retrofit2.Response
import javax.inject.Inject

class PaymentRemoteDataSource @Inject constructor(
    private val serviceGenerator: ServiceGenerator,
    private val context: Context
) : BaseRemoteDataSource(),
    PaymentRemoteData {
    override suspend fun payByToken(amount: Double, token: String): Response<ResponseObject<PayByTokenResponseDTO>> {
        val service = serviceGenerator.createService(PaymentService::class.java)
        return PayByTokenRequestDTO(
            auth.uid!!,
            amount,
            "pay order",
            token
        ).let {
            service.payByToken(it)
        }
    }

    override suspend fun connectBankAccount(): Response<ResponseObject<CreateTokenResponseDTO>> {
        val service = serviceGenerator.createService(PaymentService::class.java)
        return CreateTokenRequestDTO(auth.uid!!, "create token").let { dto ->
            service.createToken(dto)
        }
    }

    override suspend fun getBankAccountList(): Response<ResponseObject<List<BankAccountResponseDTO>>> {
        val service = serviceGenerator.createService(PaymentService::class.java)
        return GetBankAccountListRequest(auth.uid!!).let {
            service.getBankAccountList(it)
        }
    }

}