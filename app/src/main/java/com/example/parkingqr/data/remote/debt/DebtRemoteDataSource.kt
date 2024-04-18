package com.example.parkingqr.data.remote.debt

import android.content.Context
import com.example.parkingqr.data.remote.BaseRemoteDataSource
import com.example.parkingqr.data.remote.Params
import com.example.parkingqr.data.remote.State
import com.example.parkingqr.data.remote.dto.debt.InvoiceDebtFirebase
import com.example.parkingqr.data.remote.dto.invoice.ParkingInvoiceFirebase
import com.example.parkingqr.utils.TimeUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class DebtRemoteDataSource @Inject constructor(val context: Context) : BaseRemoteDataSource(),
    DebtRemoteData {
    override fun getUserUnpaidDebtInvoice(): Flow<State<List<InvoiceDebtFirebase>>> = flow {
        emit(State.loading())
        val list = mutableListOf<InvoiceDebtFirebase>()
        db.collection(Params.DEBT_INVOICE_PATH_COLLECTION)
            .whereEqualTo("parkingInvoice.user.userId", auth.uid)
            .whereEqualTo("status", InvoiceDebtFirebase.STATUS_UN_PAID)
            .get().await().let { querySnapshot ->
                for (snapshot in querySnapshot) {
                    snapshot.toObject(InvoiceDebtFirebase::class.java).let { invoiceDebtFirebase ->
                        list.add(invoiceDebtFirebase)
                    }
                }
            }
        emit(State.success(list.toList()))
    }.catch {
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    override fun createDebtInvoice(parkingInvoiceFirebase: ParkingInvoiceFirebase): Flow<State<Boolean>> =
        flow {
            emit(State.loading())
            val ref = db.collection(Params.DEBT_INVOICE_PATH_COLLECTION)
            InvoiceDebtFirebase(
                id = ref.id,
                parkingInvoiceFirebase = parkingInvoiceFirebase,
                status = InvoiceDebtFirebase.STATUS_UN_PAID,
                createAt = TimeUtil.getCurrentTime().toString()
            ).let {
                ref.document(it.id ?: "0").set(it).await()
                emit(State.success(true))
            }
        }.catch {
            emit(State.failed(it.message.toString()))
        }.flowOn(Dispatchers.IO)

}