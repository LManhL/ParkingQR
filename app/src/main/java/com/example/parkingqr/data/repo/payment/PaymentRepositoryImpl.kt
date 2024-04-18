package com.example.parkingqr.data.repo.payment

import com.example.parkingqr.data.mapper.mapToBankAccount
import com.example.parkingqr.data.mapper.mapToCreateTokenResponse
import com.example.parkingqr.data.mapper.mapToPayByTokenResponse
import com.example.parkingqr.data.remote.State
import com.example.parkingqr.data.remote.payment.PaymentRemoteData
import com.example.parkingqr.domain.model.payment.BankAccount
import com.example.parkingqr.domain.model.payment.CreateTokenResponse
import com.example.parkingqr.domain.model.payment.PayByTokenResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class PaymentRepositoryImpl @Inject constructor(private val paymentRemoteData: PaymentRemoteData) :
    PaymentRepository {
    override fun connectBankAccount(): Flow<State<CreateTokenResponse>> =
        flow<State<CreateTokenResponse>> {
            emit(State.loading())
            paymentRemoteData.connectBankAccount().let { response ->
                if (response.isSuccessful) {
                    response.body()?.data?.mapToCreateTokenResponse()?.let {
                        emit(State.success(it))
                    }
                } else {
                    emit(State.failed(response.message()))
                }
            }
        }.catch {
            emit(State.failed(it.message.toString()))
        }.flowOn(Dispatchers.IO)

    override fun getBankAccountList(): Flow<State<List<BankAccount>>> =
        flow<State<List<BankAccount>>> {
            emit(State.loading())
            paymentRemoteData.getBankAccountList().let { response ->
                if (response.isSuccessful) {
                    response.body()?.data?.map { it.mapToBankAccount() }?.let { list ->
                        emit(State.success(list))
                    }
                } else {
                    emit(State.failed(response.message()))
                }
            }
        }.catch {
            emit(State.failed(it.message.toString()))
        }.flowOn(Dispatchers.IO)

    override fun payByToken(amount: Double, token: String): Flow<State<PayByTokenResponse>> =
        flow<State<PayByTokenResponse>> {
            emit(State.loading())
            paymentRemoteData.payByToken(amount, token).let { response ->
                if (response.isSuccessful) {
                    response.body()?.data?.let {
                        emit(State.success(it.mapToPayByTokenResponse()))
                    }
                } else {
                    emit(State.failed(response.message()))
                }
            }
        }.catch {
            emit(State.failed(it.message.toString()))
        }.flowOn(Dispatchers.IO)

}