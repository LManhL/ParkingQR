package com.example.parkingqr.data.remote

import com.example.parkingqr.data.remote.model.parking.parkinginvoice.ParkingInvoiceFirebase
import com.example.parkingqr.domain.parking.ParkingInvoice
import com.example.parkingqr.domain.parking.User
import com.example.parkingqr.domain.parking.Vehicle
import kotlinx.coroutines.flow.Flow

interface IRemoteDataSource {
    fun searchLicensePlate(licensePlate: String): Flow<State<MutableList<Vehicle>>>
    fun searchUserById(userId: String): Flow<State<MutableList<User>>>
    fun addNewParkingInvoice(parkingInvoice: ParkingInvoice): Flow<State<String>>
    fun searchParkingInvoiceById(id: String): Flow<State<MutableList<ParkingInvoice>>>
    fun getNewParkingInvoiceKey(): String
    fun completeParkingInvoice(id: String): Flow<State<String>>

    fun searchParkingInvoiceByLicensePlateAndStateParking(licensePlate: String): Flow<State<Boolean>>
}