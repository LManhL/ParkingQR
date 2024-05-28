package com.example.parkingqr.data.remote.parkinglot

import android.content.Context
import android.graphics.Bitmap
import androidx.core.net.toUri
import com.example.parkingqr.data.remote.BaseRemoteDataSource
import com.example.parkingqr.data.remote.Params
import com.example.parkingqr.data.remote.State
import com.example.parkingqr.data.remote.dto.invoice.WaitingRateFirebase
import com.example.parkingqr.data.remote.dto.parkinglot.*
import com.example.parkingqr.domain.model.parkinglot.ParkingLot
import com.example.parkingqr.utils.ImageUtil
import com.example.parkingqr.utils.TimeUtil
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import javax.inject.Inject

class ParkingLotRemoteDataSource @Inject constructor(val context: Context) : BaseRemoteDataSource(),
    ParkingLotRemoteData {
    override fun getParkingLotList(): Flow<State<MutableList<ParkingLotFirebase>>> = flow {
        emit(State.loading())
        val parkingInvoiceRef = db.collection(Params.PARKING_LOT_PATH_COLLECTION)
        parkingInvoiceRef.orderBy("status").snapshots().map { querySnapshots ->
            val parkingLotList = mutableListOf<ParkingLotFirebase>()
            for (document in querySnapshots) {
                document.toObject(ParkingLotFirebase::class.java).let {
                    parkingLotList.add(it)
                }
            }
            emit(State.success(parkingLotList))
        }.collect()
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

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun createParkingLot(parkingLot: ParkingLotFirebase): Flow<State<String>> = flow {
        emit(State.loading())
        val ref = db.collection(Params.PARKING_LOT_PATH_COLLECTION).document()
        parkingLot.id = ref.id

        // Upload file to fire store
        val storageRef = storage.reference
        val uriList = mutableListOf<String>()
        parkingLot.images.asFlow().flatMapMerge { filePath ->
            flow {
                val file = filePath.toUri()
                val vehicleRegisterRef =
                    storageRef.child("${Params.PARKING_LOT_STORAGE_PATH}/${parkingLot.id}/${file.lastPathSegment}")
                vehicleRegisterRef.putFile(file).await()
                val url = vehicleRegisterRef.downloadUrl.await()
                emit(url.toString())
            }
        }.collect {
            uriList.add(it)
        }
        parkingLot.images.apply {
            clear()
            addAll(uriList)
        }

        ref.set(parkingLot).await()
        emit(State.success(parkingLot.id ?: "0"))
    }.catch {
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    override fun createBillingType(
        parkingLotId: String,
        billingTypeFirebase: BillingTypeFirebase
    ): Flow<State<Boolean>> = flow {
        emit(State.loading())
        val ref = db.collection(Params.PARKING_LOT_PATH_COLLECTION).document(parkingLotId)
            .collection(Params.BILLING_TYPE_PATH_COLLECTION).document()
        billingTypeFirebase.id = ref.id
        ref.set(billingTypeFirebase)
        emit(State.success(true))
    }.catch {
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    override fun getAllMonthlyTicketTypeList(parkingLotId: String): Flow<State<List<MonthlyTicketTypeFirebase>>> =
        flow {
            emit(State.loading())
            db.collection(Params.PARKING_LOT_PATH_COLLECTION).document(parkingLotId)
                .collection(Params.MONTHLY_TICKET_TYPE_COLLECTION).orderBy("numberOfMonth")
                .snapshots()
                .map { querySnapshots ->
                    val list = mutableListOf<MonthlyTicketTypeFirebase>()
                    for (document in querySnapshots) {
                        document.toObject(MonthlyTicketTypeFirebase::class.java).let {
                            list.add(it)
                        }
                    }
                    emit(State.success(list.toList()))
                }.collect()
        }.catch {
            emit(State.failed(it.message.toString()))
        }.flowOn(Dispatchers.IO)

    override fun updateMonthlyTicketType(
        parkingLotId: String,
        monthlyTicketTypeFirebase: MonthlyTicketTypeFirebase
    ): Flow<State<Boolean>> = flow {
        emit(State.loading())
        db.collection(Params.PARKING_LOT_PATH_COLLECTION).document(parkingLotId)
            .collection(Params.MONTHLY_TICKET_TYPE_COLLECTION)
            .document(monthlyTicketTypeFirebase.id.toString()).set(monthlyTicketTypeFirebase)
            .await()
        emit(State.success(true))
    }.catch {
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    override fun deleteMonthlyTicketType(
        parkingLotId: String,
        monthlyTicketTypeFirebase: MonthlyTicketTypeFirebase
    ): Flow<State<Boolean>> = flow {
        emit(State.loading())
        db.collection(Params.PARKING_LOT_PATH_COLLECTION).document(parkingLotId)
            .collection(Params.MONTHLY_TICKET_TYPE_COLLECTION)
            .document(monthlyTicketTypeFirebase.id.toString()).delete()
            .await()
        emit(State.success(true))
    }.catch {
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    override fun createMonthlyTicketType(
        parkingLotId: String,
        monthlyTicketTypeFirebase: MonthlyTicketTypeFirebase
    ): Flow<State<Boolean>> = flow {
        emit(State.loading())
        val ref = db.collection(Params.PARKING_LOT_PATH_COLLECTION).document(parkingLotId)
            .collection(Params.MONTHLY_TICKET_TYPE_COLLECTION).document()
        monthlyTicketTypeFirebase.id = ref.id
        ref.set(monthlyTicketTypeFirebase).await()
        emit(State.success(true))
    }.catch {
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    override fun acceptParkingLotById(parkingLotId: String): Flow<State<Boolean>> = flow {
        emit(State.loading())
        val ref = db.collection(Params.PARKING_LOT_PATH_COLLECTION).document(parkingLotId)
        ref.update("status", ParkingLotFirebase.ACCEPTED_STATUS).await()
        emit(State.success(true))
    }.catch {
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    override fun declineParkingLotById(parkingLotId: String): Flow<State<Boolean>> = flow {
        emit(State.loading())
        val ref = db.collection(Params.PARKING_LOT_PATH_COLLECTION).document(parkingLotId)
        ref.update("status", ParkingLotFirebase.DECLINED_STATUS).await()
        emit(State.success(true))
    }.catch { emit(State.failed(it.message.toString())) }.flowOn(Dispatchers.IO)

    override fun deleteParkingLotById(parkingLotId: String): Flow<State<Boolean>> = flow {
        emit(State.loading())
        val ref = db.collection(Params.PARKING_LOT_PATH_COLLECTION).document(parkingLotId)
        ref.delete().await()
        emit(State.success(true))
    }.catch { emit(State.failed(it.message.toString())) }.flowOn(Dispatchers.IO)

    override fun searchParkingLotByName(name: String): Flow<State<List<ParkingLotFirebase>>> =
        flow {
            emit(State.loading())
            val query: Query = db.collection(Params.PARKING_LOT_PATH_COLLECTION)
                .whereGreaterThanOrEqualTo("name", name)
                .whereLessThanOrEqualTo("name", "${name}~")
            query.get().await().let { querySnapshots ->
                val res = mutableListOf<ParkingLotFirebase>()
                for (snapshot in querySnapshots) {
                    res.add(snapshot.toObject(ParkingLotFirebase::class.java))
                }
                emit(State.success(res.toList()))
            }
        }.catch {
            emit(State.failed(it.message.toString()))
        }.flowOn(Dispatchers.IO)

    override fun addCamera(
        parkingLotId: String,
        securityCameraFirebase: SecurityCameraFirebase
    ): Flow<State<Boolean>> = flow {
        emit(State.loading())
        val ref = db.collection(Params.PARKING_LOT_PATH_COLLECTION).document(parkingLotId)
            .collection(Params.CAMERA_PATH_COLLECTION).document()
        securityCameraFirebase.id = ref.id
        ref.set(securityCameraFirebase).await()
        emit(State.success(true))
    }.catch {
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    override fun updateCamera(
        parkingLotId: String,
        securityCameraFirebase: SecurityCameraFirebase
    ): Flow<State<Boolean>> = flow {
        emit(State.loading())
        val ref = db.collection(Params.PARKING_LOT_PATH_COLLECTION).document(parkingLotId)
            .collection(Params.CAMERA_PATH_COLLECTION)
            .document(securityCameraFirebase.id.toString())
        ref.set(securityCameraFirebase).await()
        emit(State.success(true))
    }.catch {
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    override fun deleteCameraById(
        parkingLotId: String,
        securityCameraId: String
    ): Flow<State<Boolean>> = flow {
        emit(State.loading())
        val ref = db.collection(Params.PARKING_LOT_PATH_COLLECTION).document(parkingLotId)
            .collection(Params.CAMERA_PATH_COLLECTION).document(securityCameraId)
        ref.delete().await()
        emit(State.success(true))
    }.catch {
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    override fun getCameraIn(parkingLotId: String): Flow<State<SecurityCameraFirebase>> =
        flow<State<SecurityCameraFirebase>> {
            emit(State.loading())
            db.collection(Params.PARKING_LOT_PATH_COLLECTION).document(parkingLotId)
                .collection(Params.CAMERA_PATH_COLLECTION)
                .whereEqualTo("type", SecurityCameraFirebase.TYPE_CAM_IN).get().await()
                .let { snapshots ->
                    val res = mutableListOf<SecurityCameraFirebase>()
                    snapshots.forEach {
                        res.add(
                            it.toObject(SecurityCameraFirebase::class.java)
                        )
                    }
                    val data = res.firstOrNull()
                    if (data != null) {
                        emit(State.success(data))
                    } else {
                        emit(State.failed("Không tìm thấy"))
                    }
                }
        }.catch {
            emit(State.failed(it.message.toString()))
        }.flowOn(Dispatchers.IO)

    override fun getCameraOut(parkingLotId: String): Flow<State<SecurityCameraFirebase>> =
        flow<State<SecurityCameraFirebase>> {
            emit(State.loading())
            db.collection(Params.PARKING_LOT_PATH_COLLECTION).document(parkingLotId)
                .collection(Params.CAMERA_PATH_COLLECTION)
                .whereEqualTo("type", SecurityCameraFirebase.TYPE_CAM_OUT).get().await()
                .let { snapshots ->
                    val res = mutableListOf<SecurityCameraFirebase>()
                    snapshots.forEach {
                        res.add(
                            it.toObject(SecurityCameraFirebase::class.java)
                        )
                    }
                    val data = res.firstOrNull()
                    if (data != null) {
                        emit(State.success(data))
                    } else {
                        emit(State.failed("Không tìm thấy"))
                    }
                }
        }.catch { emit(State.failed(it.message.toString())) }.flowOn(Dispatchers.IO)

    override fun getAllCameras(parkingLotId: String): Flow<State<List<SecurityCameraFirebase>>> =
        flow {
            emit(State.loading())
            db.collection(Params.PARKING_LOT_PATH_COLLECTION).document(parkingLotId)
                .collection(Params.CAMERA_PATH_COLLECTION).get().await()
                .let { snapshots ->
                    val res = mutableListOf<SecurityCameraFirebase>()
                    snapshots.forEach {
                        res.add(
                            it.toObject(SecurityCameraFirebase::class.java)
                        )
                    }
                    emit(State.success(res.toList()))
                }
        }.catch { emit(State.failed(it.message.toString())) }.flowOn(Dispatchers.IO)
}

