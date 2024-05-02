package com.example.parkingqr.data.remote.invoice

import android.content.Context
import android.graphics.Bitmap
import com.example.parkingqr.data.remote.BaseRemoteDataSource
import com.example.parkingqr.data.remote.Params
import com.example.parkingqr.data.remote.State
import com.example.parkingqr.data.remote.dto.invoice.ParkingInvoiceFirebase
import com.example.parkingqr.data.remote.dto.invoice.WaitingRateFirebase
import com.example.parkingqr.data.remote.dto.parkinglot.UserRateFirebase
import com.example.parkingqr.data.remote.dto.vehicle.VehicleFirebase
import com.example.parkingqr.utils.ImageUtil
import com.example.parkingqr.utils.TimeUtil
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import javax.inject.Inject

class InvoiceRemoteDataSource @Inject constructor(val context: Context) : BaseRemoteDataSource(),
    InvoiceRemoteData {
    override fun searchLicensePlateByUserId(
        licensePlate: String, userId: String
    ): Flow<State<MutableList<VehicleFirebase>>> = flow {
        val vehicleRef = db.collection(Params.VEHICLE_PATH_COLLECTION)
        val query =
            vehicleRef.whereEqualTo("licensePlate", licensePlate).whereEqualTo("userId", userId)
        emit(State.loading())
        val querySnapshot = query.get().await()
        val vehicleList: MutableList<VehicleFirebase> = mutableListOf()
        for (document in querySnapshot.documents) {
            document.toObject(VehicleFirebase::class.java)?.let { vehicleList.add(it) }
        }
        emit(State.success(vehicleList))
    }.catch {
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    override fun addNewParkingInvoice(parkingInvoice: ParkingInvoiceFirebase): Flow<State<String>> =
        flow {
            val parkingInvoiceRef = db.collection(Params.PARKING_INVOICE_PATH_COLLECTION)
            emit(State.loading())

            val storageRef = storage.reference
            val bitmap = ImageUtil.decodeImage(parkingInvoice.imageIn ?: "0")
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()
            val vehicleRegisterRef =
                storageRef.child("${parkingInvoice.parkingLotId}/${Params.PARKING_INVOICE_STORAGE_PATH}/${parkingInvoice.id}/${TimeUtil.getCurrentTime()}")
            var uploadTask = vehicleRegisterRef.putBytes(data).await()
            val url = vehicleRegisterRef.downloadUrl.await()
            parkingInvoice.imageIn = url.toString()
            parkingInvoiceRef.document(parkingInvoice.id!!).set(parkingInvoice).await()
            emit(State.success("${parkingInvoiceRef.path}/${parkingInvoice.id!!}"))
        }.catch {
            emit(State.failed(it.message.toString()))
        }.flowOn(Dispatchers.IO)

    override fun searchParkingInvoiceById(id: String): Flow<State<ParkingInvoiceFirebase>> =
        flow {
            val parkingInvoiceRef = db.collection(Params.PARKING_INVOICE_PATH_COLLECTION)
            val query = parkingInvoiceRef.whereEqualTo("id", id)
            emit(State.loading())
            val querySnapshot = query.get().await()
            val parkingInvoiceList: MutableList<ParkingInvoiceFirebase> = mutableListOf()
            for (document in querySnapshot.documents) {
                document.toObject(ParkingInvoiceFirebase::class.java)
                    ?.let { parkingInvoiceList.add(it) }
            }
            val foundInvoice = parkingInvoiceList.firstOrNull()
            if (foundInvoice != null) {
                emit(State.success(foundInvoice))
            } else {
                emit(State.failed("Không tìm thấy hóa đơn"))
            }
        }.catch {
            emit(State.failed(it.message.toString()))
        }.flowOn(Dispatchers.IO)

    override fun getNewParkingInvoiceKey(): String {
        val parkingInvoiceRef = db.collection(Params.PARKING_INVOICE_PATH_COLLECTION)
        return parkingInvoiceRef.document().id
    }

    override fun completeParkingInvoice(parkingInvoice: ParkingInvoiceFirebase): Flow<State<String>> =
        flow {
            val parkingInvoiceRef = db.collection(Params.PARKING_INVOICE_PATH_COLLECTION)
            emit(State.loading())
            val storageRef = storage.reference
            var url = ""
            if (!parkingInvoice.imageOut.isNullOrEmpty()) {
                val bitmap = ImageUtil.decodeImage(parkingInvoice.imageOut!!)
                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val data = baos.toByteArray()
                val vehicleRegisterRef =
                    storageRef.child("${auth.currentUser?.uid}/${Params.PARKING_INVOICE_STORAGE_PATH}/${parkingInvoice.id}/${TimeUtil.getCurrentTime()}")
                var uploadTask = vehicleRegisterRef.putBytes(data).await()
                url = vehicleRegisterRef.downloadUrl.await().toString()
            }
            parkingInvoice.imageIn = url

            parkingInvoiceRef.document(parkingInvoice.id!!).update(
                "state",
                "parked",
                "imageOut",
                url,
                "note",
                parkingInvoice.note,
                "timeOut",
                parkingInvoice.timeOut
            ).await()

            emit(State.success("${parkingInvoiceRef.path}/${parkingInvoice.id}"))
        }.catch {
            emit(State.failed(it.message.toString()))
        }.flowOn(Dispatchers.IO)

    override fun refuseParkingInvoice(id: String): Flow<State<String>> = flow {
        emit(State.loading())
        val parkingInvoiceRef = db.collection(Params.PARKING_INVOICE_PATH_COLLECTION)
        parkingInvoiceRef.document(id).update("state", "refused").await()
        emit(State.success("${parkingInvoiceRef.path}/${id}"))
    }.catch {
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    override fun getParkingLotInvoiceList(parkingLotId: String): Flow<State<MutableList<ParkingInvoiceFirebase>>> =
        flow {
            emit(State.loading())
            val parkingInvoiceRef = db.collection(Params.PARKING_INVOICE_PATH_COLLECTION)
            val query: Query = parkingInvoiceRef.whereEqualTo("parkingLotId", parkingLotId)
            val querySnapshot = query.get().await()
            val parkingInvoiceList = mutableListOf<ParkingInvoiceFirebase>()
            for (document in querySnapshot.documents) {
                document.toObject(ParkingInvoiceFirebase::class.java)?.let {
                    parkingInvoiceList.add(it)
                }
            }
            emit(State.success(parkingInvoiceList))
        }.catch {
            emit(State.failed(it.message.toString()))
        }.flowOn(Dispatchers.IO)

    override fun getParkingInvoiceById(id: String): Flow<State<MutableList<ParkingInvoiceFirebase>>> =
        flow {
            val parkingInvoiceRef = db.collection(Params.PARKING_INVOICE_PATH_COLLECTION)
            val query = parkingInvoiceRef.whereEqualTo("id", id)
            emit(State.loading())
            val querySnapshot = query.get().await()
            val parkingInvoiceList: MutableList<ParkingInvoiceFirebase> = mutableListOf()
            for (document in querySnapshot.documents) {
                document.toObject(ParkingInvoiceFirebase::class.java)?.let {
                    parkingInvoiceList.add(it)
                }
            }
            emit(State.success(parkingInvoiceList))
        }.catch {
            emit(State.failed(it.message.toString()))
        }.flowOn(Dispatchers.IO)

    override fun updateParkingInvoice(parkingInvoice: ParkingInvoiceFirebase): Flow<State<Boolean>> =
        flow {
            emit(State.loading())
            val parkingInvoiceRef = db.collection(Params.PARKING_INVOICE_PATH_COLLECTION)
            parkingInvoiceRef.document(parkingInvoice.id!!).update(
                "type",
                parkingInvoice.type,
                "note",
                parkingInvoice.note,
                "paymentMethod",
                parkingInvoice.paymentMethod
            ).await()
            emit(State.success(true))
        }.catch {
            emit(State.failed(it.message.toString()))
        }.flowOn(Dispatchers.IO)

    override fun getUserParkingInvoiceList(): Flow<State<MutableList<ParkingInvoiceFirebase>>> =
        flow {
            emit(State.loading())
            val parkingInvoiceRef = db.collection(Params.PARKING_INVOICE_PATH_COLLECTION)
            val query: Query =
                parkingInvoiceRef.whereEqualTo("user.userId", auth.currentUser?.uid).whereNotIn(
                    "state", mutableListOf("parking")
                )
            val querySnapshot = query.get().await()
            val parkingInvoiceList = mutableListOf<ParkingInvoiceFirebase>()
            for (document in querySnapshot.documents) {
                document.toObject(ParkingInvoiceFirebase::class.java)?.let {
                    parkingInvoiceList.add(it)
                }
            }
            emit(State.success(parkingInvoiceList))
        }.catch {
            emit(State.failed(it.message.toString()))
        }.flowOn(Dispatchers.IO)

    override fun searchParkingInvoiceUser(licensePlate: String): Flow<State<MutableList<ParkingInvoiceFirebase>>> =
        flow {
            emit(State.loading())
            val parkingInvoiceRef = db.collection(Params.PARKING_INVOICE_PATH_COLLECTION)
            val query: Query = parkingInvoiceRef.whereEqualTo("user.userId", auth.currentUser?.uid)
                .whereGreaterThanOrEqualTo("vehicle.licensePlate", licensePlate.uppercase())
                .whereLessThanOrEqualTo("vehicle.licensePlate", "${licensePlate.uppercase()}~")
            val querySnapshot = query.get().await()
            val parkingInvoiceList = mutableListOf<ParkingInvoiceFirebase>()
            for (document in querySnapshot.documents) {
                document.toObject(ParkingInvoiceFirebase::class.java)?.let {
                    parkingInvoiceList.add(it)
                }
            }
            emit(State.success(parkingInvoiceList))
        }.catch { emit(State.failed(it.message.toString())) }.flowOn(Dispatchers.IO)

    override fun searchParkingInvoiceParkingLot(
        licensePlate: String, parkingLotId: String
    ): Flow<State<MutableList<ParkingInvoiceFirebase>>> = flow {
        emit(State.loading())
        val parkingInvoiceRef = db.collection(Params.PARKING_INVOICE_PATH_COLLECTION)
        val query: Query = parkingInvoiceRef.whereEqualTo("parkingLotId", parkingLotId)
            .whereGreaterThanOrEqualTo("vehicle.licensePlate", licensePlate.uppercase())
            .whereLessThanOrEqualTo("vehicle.licensePlate", "${licensePlate.uppercase()}~")
        val querySnapshot = query.get().await()
        val parkingInvoiceList = mutableListOf<ParkingInvoiceFirebase>()
        for (document in querySnapshot.documents) {
            document.toObject(ParkingInvoiceFirebase::class.java)?.let {
                parkingInvoiceList.add(it)
            }
        }
        emit(State.success(parkingInvoiceList))
    }.catch { emit(State.failed(it.message.toString())) }.flowOn(Dispatchers.IO)

    override fun searchParkingInvoiceByLicensePlateAndStateParking(licensePlate: String): Flow<State<Boolean>> =
        flow {
            emit(State.loading())
            val parkingInvoiceRef = db.collection(Params.PARKING_INVOICE_PATH_COLLECTION)
            val query: Query = parkingInvoiceRef.whereEqualTo("state", "parking")
                .whereEqualTo("vehicle.licensePlate", licensePlate)
            val querySnapshot = query.get().await()
            if (querySnapshot.documents.isNotEmpty()) {
                emit(State.success(true))
            } else emit(State.success(false))
        }.catch {
            emit(State.failed(it.message.toString()))
        }.flowOn(Dispatchers.IO)

    override fun getUserInvoiceListHaveParkingState(): Flow<State<MutableList<ParkingInvoiceFirebase>>> =
        flow {
            emit(State.loading())
            db.collection(Params.PARKING_INVOICE_PATH_COLLECTION)
                .whereEqualTo("user.userId", auth.currentUser?.uid).whereIn(
                    "state", mutableListOf("parking")
                ).snapshots().map { querySnapshots ->
                    val parkingInvoiceList = mutableListOf<ParkingInvoiceFirebase>()
                    for (snapshot in querySnapshots) {
                        snapshot.toObject(ParkingInvoiceFirebase::class.java).let {
                            parkingInvoiceList.add(it)
                        }
                    }
                    emit(State.success(parkingInvoiceList))
                }.collect()
        }.catch {
            emit(State.failed(it.message.toString()))
        }.flowOn(Dispatchers.IO)

    override fun updateInvoicePaymentMethod(parkingInvoice: ParkingInvoiceFirebase): Flow<State<Boolean>> =
        flow {
            emit(State.loading())
            val parkingInvoiceRef = db.collection(Params.PARKING_INVOICE_PATH_COLLECTION)
            parkingInvoiceRef.document(parkingInvoice.id!!).update(
                "paymentMethod", parkingInvoice.paymentMethod
            ).await()
            emit(State.success(true))
        }.catch {
            emit(State.failed(it.message.toString()))
        }.flowOn(Dispatchers.IO)

    override fun createWaitingRate(parkingInvoice: ParkingInvoiceFirebase): Flow<State<Boolean>> =
        flow {
            emit(State.loading())
            val ref = db.collection(Params.WAITING_RATE_PATH_COLLECTION).document()
            WaitingRateFirebase(
                id = ref.id,
                parkingLotId = parkingInvoice.parkingLotId,
                parkingInvoiceId = parkingInvoice.id,
                rate = 0.0,
                comment = "",
                createAt = TimeUtil.getCurrentTime().toString(),
                showed = false,
                user = UserRateFirebase(
                    userId = parkingInvoice.user?.userId,
                    name = parkingInvoice.user?.name,
                    avatar = ""
                ),
                licensePlate = parkingInvoice.vehicle?.licensePlate,
                brand = parkingInvoice.vehicle?.brand,
                vehicleType = parkingInvoice.vehicle?.type
            ).apply {
                ref.set(this)
                emit(State.success(true))
            }
        }.catch {
            emit(State.failed(it.message.toString()))
        }.flowOn(Dispatchers.IO)

    override fun getUnShowedWaitingRates(): Flow<State<List<WaitingRateFirebase>>> = flow {
        emit(State.loading())
        db.collection(Params.WAITING_RATE_PATH_COLLECTION).whereEqualTo("user.userId", auth.uid)
            .whereEqualTo("showed", false)
            .snapshots().map { querySnapshots ->
                val list = mutableListOf<WaitingRateFirebase>()
                for (snapshot in querySnapshots) {
                    list.add(snapshot.toObject(WaitingRateFirebase::class.java))
                }
                emit(State.success(list.toList()))
            }.collect()
    }.catch {
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    override fun updateShowedStateWaitingRate(waitingRate: WaitingRateFirebase): Flow<State<Boolean>> =
        flow {
            emit(State.loading())
            waitingRate.id?.let { id ->
                db.collection(Params.WAITING_RATE_PATH_COLLECTION).document(id)
                    .update("showed", true).await()
                emit(State.success(true))
            }
        }.catch {
            emit(State.failed(it.message.toString()))
        }.flowOn(Dispatchers.IO)

    override fun deleteWaitingRateById(id: String): Flow<State<Boolean>> = flow {
        emit(State.loading())
        db.collection(Params.WAITING_RATE_PATH_COLLECTION).document(id).delete().await()
        emit(State.success(true))
    }.catch {
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

}