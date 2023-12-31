package com.example.parkingqr.data.remote

import com.example.parkingqr.domain.invoice.ParkingInvoiceIV
import com.example.parkingqr.domain.parking.ParkingInvoicePK
import com.example.parkingqr.domain.parking.User
import com.example.parkingqr.domain.parking.Vehicle
import kotlinx.coroutines.flow.Flow

interface IRemoteDataSource {
    fun searchLicensePlate(licensePlate: String): Flow<State<MutableList<Vehicle>>>
    fun searchUserById(userId: String): Flow<State<MutableList<User>>>
    fun addNewParkingInvoice(parkingInvoicePK: ParkingInvoicePK): Flow<State<String>>
    fun searchParkingInvoiceById(id: String): Flow<State<MutableList<ParkingInvoicePK>>>
    fun getNewParkingInvoiceKey(): String
    fun completeParkingInvoice(id: String): Flow<State<String>>
    fun searchParkingInvoiceByLicensePlateAndStateParking(licensePlate: String): Flow<State<Boolean>>

    fun getParkingInvoiceList(id: String): Flow<State<MutableList<ParkingInvoiceIV>>>
    fun getParkingInvoiceById(id: String): Flow<State<MutableList<ParkingInvoiceIV>>>
}