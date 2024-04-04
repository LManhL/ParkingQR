package com.example.parkingqr.data.remote.monthlyticket

import android.content.Context
import com.example.parkingqr.data.remote.BaseRemoteDataSource
import com.example.parkingqr.data.remote.Params
import com.example.parkingqr.data.remote.State
import com.example.parkingqr.data.remote.dto.parkinglot.MonthlyTicketFirebase
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class MonthlyTicketRemoteDataSource @Inject constructor(val context: Context) :
    BaseRemoteDataSource(), MonthlyTicketRemoteData {
    override fun getCurrentUserMonthlyTicketList(): Flow<State<MutableList<MonthlyTicketFirebase>>> =
        flow {
            emit(State.loading())
            val list = mutableListOf<MonthlyTicketFirebase>()
            db.collection(Params.MONTHLY_TICKET_COLLECTION).whereEqualTo("user.userId", auth.uid)
                .get().await().let { querySnapshot ->
                    for (document in querySnapshot.documents) {
                        document.toObject(MonthlyTicketFirebase::class.java)?.let {
                            list.add(it)
                        }
                    }
                    emit(State.success(list))
                }
        }.catch {
            emit(State.failed(it.message.toString()))
        }.flowOn(Dispatchers.IO)

    override fun createMonthlyTicket(monthlyTicketFirebase: MonthlyTicketFirebase): Flow<State<Boolean>> =
        flow {
            emit(State.loading())
            val ref = db.collection(Params.MONTHLY_TICKET_COLLECTION).document()
            monthlyTicketFirebase.apply {
                id = ref.id
            }.let {
                ref.set(it).await()
            }
            emit(State.success(true))
        }.catch {
            emit(State.failed(it.message.toString()))
        }.flowOn(Dispatchers.IO)

    override fun getMonthlyTicketById(monthlyTicketId: String): Flow<State<MonthlyTicketFirebase>> =
        flow {
            emit(State.loading())
            val value =
                db.collection(Params.MONTHLY_TICKET_COLLECTION).document(monthlyTicketId).get()
                    .await()
                    ?.toObject(MonthlyTicketFirebase::class.java)
            if (value != null) {
                emit(State.success(value))
            } else {
                emit(State.failed("Lỗi không xác định"))
            }

        }.catch {
            emit(State.failed(it.message.toString()))
        }.flowOn(Dispatchers.IO)
}