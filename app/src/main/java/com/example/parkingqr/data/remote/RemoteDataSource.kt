package com.example.parkingqr.data.remote

import android.util.Log
import com.example.parkingqr.data.remote.model.parking.UserResponse
import com.example.parkingqr.data.remote.model.parking.VehicleResponse
import com.example.parkingqr.data.remote.model.parking.parkinginvoice.ParkingInvoiceFirebase
import com.example.parkingqr.domain.invoice.ParkingInvoiceIV
import com.example.parkingqr.domain.parking.ParkingInvoicePK
import com.example.parkingqr.domain.parking.User
import com.example.parkingqr.domain.parking.Vehicle
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await

class RemoteDataSource : IRemoteDataSource {

    private val db = Firebase.firestore

    override fun searchLicensePlate(licensePlate: String): Flow<State<MutableList<Vehicle>>> =
        flow {
            val vehicleRef = db.collection(Params.VEHICLE_PATH_COLLECTION)
            val query = vehicleRef.whereEqualTo("licensePlate", licensePlate)
            emit(State.loading())
            val querySnapshot = query.get().await()
            val vehicleList: MutableList<Vehicle> = mutableListOf()
            for (document in querySnapshot.documents) {
                document.toObject(VehicleResponse::class.java)?.let { vehicleList.add(Vehicle(it)) }
            }
            emit(State.success(vehicleList))
        }.catch {
            emit(State.failed(it.message.toString()))
        }.flowOn(Dispatchers.IO)

    override fun searchUserById(userId: String): Flow<State<MutableList<User>>> = flow {
        val userRef = db.collection(Params.USER_PATH_COLLECTION)
        val query = userRef.whereEqualTo("userId", userId)
        emit(State.loading())
        val querySnapshot = query.get().await()
        val userList: MutableList<User> = mutableListOf()
        for (document in querySnapshot.documents) {
            document.toObject(UserResponse::class.java)?.let { userList.add(User(it)) }
        }
        emit(State.success(userList))
    }.catch {
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    override fun addNewParkingInvoice(parkingInvoicePK: ParkingInvoicePK): Flow<State<String>> = flow{
        val parkingInvoiceRef = db.collection(Params.PARKING_INVOICE_PATH_COLLECTION)
        val parkingInvoiceFirebase = ParkingInvoiceFirebase(parkingInvoicePK)
        emit(State.loading())
        parkingInvoiceRef.document(parkingInvoiceFirebase.id!!).set(parkingInvoiceFirebase).await()
        emit(State.success("${parkingInvoiceRef.path}/${parkingInvoiceFirebase.id!!}"))
    }.catch {
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    override fun searchParkingInvoiceById(id: String): Flow<State<MutableList<ParkingInvoicePK>>> = flow{
        val parkingInvoiceRef = db.collection(Params.PARKING_INVOICE_PATH_COLLECTION)
        val query = parkingInvoiceRef.whereEqualTo("id", id)
        emit(State.loading())
        val querySnapshot = query.get().await()
        val parkingInvoicePKList: MutableList<ParkingInvoicePK> = mutableListOf()
        for (document in querySnapshot.documents) {
            document.toObject(ParkingInvoiceFirebase::class.java)?.let { parkingInvoicePKList.add(ParkingInvoicePK(it)) }
        }
        emit(State.success(parkingInvoicePKList))
    }.catch {
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    override fun getNewParkingInvoiceKey(): String {
        val parkingInvoiceRef = db.collection(Params.PARKING_INVOICE_PATH_COLLECTION)
        return parkingInvoiceRef.document().id
    }

    override fun completeParkingInvoice(id: String): Flow<State<String>> = flow {
        emit(State.loading())
        val parkingInvoiceRef = db.collection(Params.PARKING_INVOICE_PATH_COLLECTION)
        parkingInvoiceRef.document(id).update("state", "parked").await()
        emit(State.success("${parkingInvoiceRef.path}/${id}"))
    }.catch {
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    override fun searchParkingInvoiceByLicensePlateAndStateParking(licensePlate: String): Flow<State<Boolean>> = flow {
        emit(State.loading())
        val parkingInvoiceRef = db.collection(Params.PARKING_INVOICE_PATH_COLLECTION)
        val query: Query = parkingInvoiceRef.whereEqualTo("state", "parking").whereEqualTo("vehicle.licensePlate",licensePlate)
        val querySnapshot = query.get().await()
        if(querySnapshot.documents.isNotEmpty()){
            emit(State.success(true))
        }
        else emit(State.success(false))
    }.catch {
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    override fun getParkingInvoiceList(id: String): Flow<State<MutableList<ParkingInvoiceIV>>> = flow {
        emit(State.loading())
        val parkingInvoiceRef = db.collection(Params.PARKING_INVOICE_PATH_COLLECTION)
        val query: Query = parkingInvoiceRef
        val querySnapshot = query.get().await()
        val parkingInvoiceIVList = mutableListOf<ParkingInvoiceIV>()
        for (document in querySnapshot.documents) {
            document.toObject(ParkingInvoiceFirebase::class.java)?.let {
                parkingInvoiceIVList.add(ParkingInvoiceIV(it))
                Log.d("CHECKKKKK", it.timeIn.toString())
            }
        }
        emit(State.success(parkingInvoiceIVList))
    }.catch {
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    override fun getParkingInvoiceById(id: String): Flow<State<MutableList<ParkingInvoiceIV>>> = flow {
        val parkingInvoiceRef = db.collection(Params.PARKING_INVOICE_PATH_COLLECTION)
        val query = parkingInvoiceRef.whereEqualTo("id", id)
        emit(State.loading())
        val querySnapshot = query.get().await()
        val parkingInvoiceList: MutableList<ParkingInvoiceIV> = mutableListOf()
        for (document in querySnapshot.documents) {
            document.toObject(ParkingInvoiceFirebase::class.java)?.let { parkingInvoiceList.add(
                ParkingInvoiceIV(it)
            ) }
        }
        emit(State.success(parkingInvoiceList))
    }.catch {
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

}