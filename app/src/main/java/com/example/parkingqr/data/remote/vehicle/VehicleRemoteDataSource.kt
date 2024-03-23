package com.example.parkingqr.data.remote.vehicle

import android.content.Context
import androidx.core.net.toUri
import com.example.parkingqr.data.remote.BaseRemoteDataSource
import com.example.parkingqr.data.remote.Params
import com.example.parkingqr.data.remote.State
import com.example.parkingqr.data.remote.dto.vehicle.VehicleFirebase
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class VehicleRemoteDataSource @Inject constructor(val context: Context) : BaseRemoteDataSource(),
    VehicleRemoteData {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun createVehicleRegistrationForm(vehicle: VehicleFirebase): Flow<State<Boolean>> =
        flow {
            emit(State.loading())
            // Create reference to fire store
            val vehicleRef = db.collection(Params.VEHICLE_PATH_COLLECTION)
            val key = vehicleRef.document().id
            vehicle.id = key
            vehicle.userId = auth.currentUser?.uid

            // Upload file to fire store
            val storageRef = storage.reference
            val uriList = mutableListOf<String>()

            vehicle.images.asFlow().flatMapMerge { filePath ->
                flow {
                    val file = filePath.toUri()
                    val vehicleRegisterRef =
                        storageRef.child("${vehicle.userId}/${Params.VEHICLE_REGISTRATION_STORAGE_PATH}/${vehicle.id}/${file.lastPathSegment}")
                    vehicleRegisterRef.putFile(file).await()
                    val url = vehicleRegisterRef.downloadUrl.await()
                    emit(url.toString())
                }
            }.collect {
                uriList.add(it)
            }

            vehicle.images.clear()
            vehicle.images.addAll(uriList)

            // Set data to fire store
            val snapshot =
                vehicleRef.document(vehicle.id!!)
                    .set(vehicle).await()
            emit(State.success(true))
        }.catch {
            emit(State.failed(it.message.toString()))
        }.flowOn(Dispatchers.IO)


    override fun getVehicleRegistrationList(): Flow<State<MutableList<VehicleFirebase>>> = flow {
        emit(State.loading())
        val ref = db.collection(Params.VEHICLE_PATH_COLLECTION)
        val query: Query = ref.whereEqualTo("userId", auth.currentUser?.uid)
        val querySnapshot = query.get().await()
        val vehicleList = mutableListOf<VehicleFirebase>()
        for (document in querySnapshot.documents) {
            document.toObject(VehicleFirebase::class.java)?.let {
                vehicleList.add(it)
            }
        }
        emit(State.success(vehicleList))
    }.catch {
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    override fun getVehicleById(id: String): Flow<State<VehicleFirebase>> = flow {
        val parkingInvoiceRef = db.collection(Params.VEHICLE_PATH_COLLECTION)
        val query = parkingInvoiceRef.whereEqualTo("id", id)
        emit(State.loading())
        val querySnapshot = query.get().await()
        val vehicleList: MutableList<VehicleFirebase> = mutableListOf()
        for (document in querySnapshot.documents) {
            document.toObject(VehicleFirebase::class.java)?.let {
                vehicleList.add(it)
            }
        }
        if (vehicleList.isNotEmpty()) {
            emit(State.success(vehicleList[0]))
        } else emit(State.failed("Lỗi không xác định"))
    }.catch {
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    override fun deleteVehicleById(id: String): Flow<State<Boolean>> = flow {
        emit(State.loading())
        val parkingInvoiceRef = db.collection(Params.VEHICLE_PATH_COLLECTION)
        parkingInvoiceRef.document(id).delete().await()
        emit(State.success(true))
    }.catch {
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    override fun getAllVehicle(): Flow<State<MutableList<VehicleFirebase>>> = flow {
        emit(State.loading())
        val ref = db.collection(Params.VEHICLE_PATH_COLLECTION)
        val query: Query = ref.orderBy("createAt")
        val querySnapshot = query.get().await()
        val vehicleList = mutableListOf<VehicleFirebase>()
        for (document in querySnapshot.documents) {
            document.toObject(VehicleFirebase::class.java)?.let {
                vehicleList.add(it)
            }
        }
        emit(State.success(vehicleList))
    }.catch { emit(State.failed(it.message.toString())) }.flowOn(Dispatchers.IO)

    override fun acceptVehicle(vehicle: VehicleFirebase): Flow<State<Boolean>> = flow {
        emit(State.loading())
        val parkingInvoiceRef = db.collection(Params.VEHICLE_PATH_COLLECTION)
        parkingInvoiceRef.document(vehicle.id!!).update("state", "verified").await()
        emit(State.success(true))
    }.catch { emit(State.failed(it.message.toString())) }.flowOn(Dispatchers.IO)

    override fun refuseVehicle(vehicle: VehicleFirebase): Flow<State<Boolean>> = flow {
        emit(State.loading())
        val parkingInvoiceRef = db.collection(Params.VEHICLE_PATH_COLLECTION)
        parkingInvoiceRef.document(vehicle.id!!).update("state", "refused").await()
        emit(State.success(true))
    }.catch { emit(State.failed(it.message.toString())) }.flowOn(Dispatchers.IO)

    override fun pendingVehicle(vehicle: VehicleFirebase): Flow<State<Boolean>> = flow {
        emit(State.loading())
        val parkingInvoiceRef = db.collection(Params.VEHICLE_PATH_COLLECTION)
        parkingInvoiceRef.document(vehicle.id!!).update("state", "unverified").await()
        emit(State.success(true))
    }.catch { emit(State.failed(it.message.toString())) }.flowOn(Dispatchers.IO)

    override fun getAllVehicleOfUserByUserId(userId: String): Flow<State<MutableList<VehicleFirebase>>> =
        flow {
            emit(State.loading())
            val ref = db.collection(Params.VEHICLE_PATH_COLLECTION)
            val query: Query = ref.whereEqualTo("userId", userId)
            val querySnapshot = query.get().await()
            val vehicleList = mutableListOf<VehicleFirebase>()
            for (document in querySnapshot.documents) {
                document.toObject(VehicleFirebase::class.java)?.let {
                    vehicleList.add(it)
                }
            }
            emit(State.success(vehicleList))
        }.catch { emit(State.failed(it.message.toString())) }.flowOn(Dispatchers.IO)

}