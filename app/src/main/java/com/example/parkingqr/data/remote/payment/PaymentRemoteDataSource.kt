package com.example.parkingqr.data.remote.payment

import android.content.Context
import com.example.parkingqr.data.remote.BaseRemoteDataSource
import com.example.parkingqr.data.remote.ServiceGenerator
import com.example.parkingqr.data.remote.payment.dto.*
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
            "1001713194174hgkjueDCnbSL0iMDPOU1RFSgq37ckubc3ebeNlyMDLWwLXERl03"
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