package com.example.parkingqr.data.remote.debt

import android.content.Context
import android.util.Log
import com.example.parkingqr.data.remote.BaseRemoteDataSource
import com.example.parkingqr.data.remote.Params
import com.example.parkingqr.data.remote.State
import com.example.parkingqr.data.remote.dto.debt.InvoiceDebtFirebase
import com.example.parkingqr.data.remote.dto.invoice.ParkingInvoiceFirebase
import com.example.parkingqr.utils.TimeUtil
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class DebtRemoteDataSource @Inject constructor(val context: Context) : BaseRemoteDataSource(),
    DebtRemoteData {
    override fun getUserUnpaidDebtInvoice(): Flow<State<InvoiceDebtFirebase>> = flow {
        emit(State.loading())
        db.collection(Params.DEBT_INVOICE_PATH_COLLECTION)
            .whereEqualTo("parkingInvoice.user.userId", auth.uid)
            .whereEqualTo("status", InvoiceDebtFirebase.STATUS_UN_PAID).snapshots().map {
                val list = mutableListOf<InvoiceDebtFirebase>()
                for (snapshot in it) {
                    list.add(snapshot.toObject(InvoiceDebtFirebase::class.java))
                }
                list.firstOrNull()?.let { debt ->
                    emit(State.success(debt))
                }
                if(list.isEmpty()){
                    emit(State.failed(""))
                }
            }.collect()
    }.catch {
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    override fun createDebtInvoice(parkingInvoiceFirebase: ParkingInvoiceFirebase): Flow<State<Boolean>> =
        flow {
            emit(State.loading())
            val ref = db.collection(Params.DEBT_INVOICE_PATH_COLLECTION).document()
            InvoiceDebtFirebase(
                id = ref.id,
                parkingInvoice = parkingInvoiceFirebase,
                status = InvoiceDebtFirebase.STATUS_UN_PAID,
                createAt = TimeUtil.getCurrentTime().toString()
            ).let {
                ref.set(it).await()
                emit(State.success(true))
            }
        }.catch {
            emit(State.failed(it.message.toString()))
        }.flowOn(Dispatchers.IO)

    override fun payDebtInvoice(invoiceDebtFirebase: InvoiceDebtFirebase): Flow<State<Boolean>> =
        flow {
            emit(State.loading())
            invoiceDebtFirebase.id?.let { id ->
                db.collection(Params.DEBT_INVOICE_PATH_COLLECTION).document(id)
                    .update("status", InvoiceDebtFirebase.STATUS_PAID).await()
                emit(State.success(true))
            }
        }.catch {
            emit(State.failed(it.message.toString()))
        }.flowOn(Dispatchers.IO)

}