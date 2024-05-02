package com.example.parkingqr.data.remote.parkinglot

import android.content.Context
import com.example.parkingqr.data.remote.BaseRemoteDataSource
import com.example.parkingqr.data.remote.Params
import com.example.parkingqr.data.remote.State
import com.example.parkingqr.data.remote.dto.invoice.WaitingRateFirebase
import com.example.parkingqr.data.remote.dto.parkinglot.*
import com.example.parkingqr.utils.TimeUtil
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ParkingLotRemoteDataSource @Inject constructor(val context: Context) : BaseRemoteDataSource(),
    ParkingLotRemoteData {
    override fun getParkingLotList(): Flow<State<MutableList<ParkingLotFirebase>>> = flow {
        emit(State.loading())
        val parkingInvoiceRef = db.collection(Params.PARKING_LOT_PATH_COLLECTION)
        val query: Query = parkingInvoiceRef
        val querySnapshot = query.get().await()
        val parkingLotList = mutableListOf<ParkingLotFirebase>()
        for (document in querySnapshot.documents) {
            document.toObject(ParkingLotFirebase::class.java)?.let {
                parkingLotList.add(it)
            }
        }
        emit(State.success(parkingLotList))
    }.catch {
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    override fun getParkingLotRates(parkingLotId: String): Flow<State<MutableList<RateFirebase>>> =
        flow {
            emit(State.loading())
            val rateRef = db.collection(Params.PARKING_LOT_PATH_COLLECTION).document(parkingLotId)
                .collection(Params.RATE_PATH_COLLECTION)
            val query: Query = rateRef
            val querySnapshot = query.get().await()
            val rates = mutableListOf<RateFirebase>()
            for (document in querySnapshot.documents) {
                document.toObject(RateFirebase::class.java)?.let {
                    rates.add(it)
                }
            }
            emit(State.success(rates))
        }.catch {
            emit(State.failed(it.message.toString()))
        }.flowOn(Dispatchers.IO)

    override fun getParkingLotById(id: String): Flow<State<ParkingLotFirebase>> = flow {
        emit(State.loading())
        val parkingLotRef = db.collection(Params.PARKING_LOT_PATH_COLLECTION).document(id)
        val snapshot = parkingLotRef.get().await()
        var parkingLot = ParkingLotFirebase()
        snapshot.toObject(ParkingLotFirebase::class.java)?.let {
            parkingLot = it
        }
        emit(State.success(parkingLot))
    }.catch {
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    override fun getBillingTypesByParkingLotId(parkingLotId: String): Flow<State<MutableList<BillingTypeFirebase>>> =
        flow {
            emit(State.loading())
            val ref = db.collection(Params.PARKING_LOT_PATH_COLLECTION).document(parkingLotId)
                .collection(Params.BILLING_TYPE_PATH_COLLECTION)
            val query: Query = ref
            val querySnapshot = query.get().await()
            val billingTypes = mutableListOf<BillingTypeFirebase>()
            for (document in querySnapshot.documents) {
                document.toObject(BillingTypeFirebase::class.java)?.let {
                    billingTypes.add(it)
                }
            }
            emit(State.success(billingTypes))
        }.catch {
            emit(State.failed(it.message.toString()))
        }.flowOn(Dispatchers.IO)

    override fun updateBillingType(
        parkingLotId: String,
        billingTypeFirebase: BillingTypeFirebase
    ): Flow<State<Boolean>> =
        flow {
            emit(State.loading())
            val ref = db.collection(Params.PARKING_LOT_PATH_COLLECTION).document(parkingLotId)
                .collection(Params.BILLING_TYPE_PATH_COLLECTION)
            ref.document(billingTypeFirebase.id.toString()).set(billingTypeFirebase)
            emit(State.success(true))
        }.catch {
            emit(State.failed(it.message.toString()))
        }.flowOn(Dispatchers.IO)

    override fun getMonthlyTicketList(
        parkingLotId: String,
        vehicleType: String
    ): Flow<State<MutableList<MonthlyTicketTypeFirebase>>> =
        flow {
            emit(State.loading())
            val ref = db.collection(Params.PARKING_LOT_PATH_COLLECTION).document(parkingLotId)
                .collection(Params.MONTHLY_TICKET_TYPE_COLLECTION)
            val query: Query = ref.whereEqualTo("vehicleType", vehicleType).orderBy("numberOfMonth")
            val querySnapshot = query.get().await()
            val list = mutableListOf<MonthlyTicketTypeFirebase>()
            for (document in querySnapshot.documents) {
                document.toObject(MonthlyTicketTypeFirebase::class.java)?.let {
                    list.add(it)
                }
            }
            emit(State.success(list))
        }.catch {
            emit(State.failed(it.message.toString()))
        }.flowOn(Dispatchers.IO)

    override fun createRate(waitingRateFirebase: WaitingRateFirebase): Flow<State<Boolean>> = flow {
        emit(State.loading())
        val ref = db.collection(Params.PARKING_LOT_PATH_COLLECTION)
            .document(waitingRateFirebase.parkingLotId ?: "0")
            .collection(Params.RATE_PATH_COLLECTION).document()
        RateFirebase(
            id = ref.id,
            parkingLotId = waitingRateFirebase.parkingLotId,
            parkingInvoiceId = waitingRateFirebase.id,
            rate = waitingRateFirebase.rate,
            comment = waitingRateFirebase.comment,
            createAt = TimeUtil.getCurrentTime().toString(),
            user = waitingRateFirebase.user?.apply {
                avatar =
                    "https://media.npr.org/assets/img/2023/12/12/gettyimages-1054147940-f05675101c3cea817f5ecbb4a7b0cc827f0d9a10-s1100-c50.jpg"
            },
            licensePlate = waitingRateFirebase.licensePlate,
            brand = waitingRateFirebase.brand,
            vehicleType = waitingRateFirebase.vehicleType
        ).let {
            ref.set(it).await()
            emit(State.success(true))
        }
    }.catch {
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

}