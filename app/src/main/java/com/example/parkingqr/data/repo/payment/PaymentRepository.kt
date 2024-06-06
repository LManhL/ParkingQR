package com.example.parkingqr.data.repo.payment

import com.example.parkingqr.data.remote.State
import com.example.parkingqr.domain.model.payment.BankAccount
import com.example.parkingqr.domain.model.payment.CreateTokenResponse
import com.example.parkingqr.domain.model.payment.PayByTokenResponse
import kotlinx.coroutines.flow.Flow

interface PaymentRepository {
    fun connectBankAccount(): Flow<State<CreateTokenResponse>>
    fun getBankAccountList(): Flow<State<List<BankAccount>>>
    fun payByToken(amount: Double, token: String): Flow<State<PayByTokenResponse>>
}