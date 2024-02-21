package com.example.parkingqr.data.remote.invoice

import com.example.parkingqr.data.remote.State
import com.example.parkingqr.data.remote.dto.invoice.ParkingInvoiceFirebase
import com.example.parkingqr.data.remote.dto.vehicle.VehicleFirebase
import kotlinx.coroutines.flow.Flow

interface InvoiceRemoteData {
    fun searchLicensePlate(licensePlate: String): Flow<State<MutableList<VehicleFirebase>>>
    fun addNewParkingInvoice(parkingInvoice: ParkingInvoiceFirebase): Flow<State<String>>
    fun searchParkingInvoiceById(id: String): Flow<State<MutableList<ParkingInvoiceFirebase>>>
    fun getNewParkingInvoiceKey(): String
    fun completeParkingInvoice(parkingInvoice: ParkingInvoiceFirebase): Flow<State<String>>
    fun refuseParkingInvoice(id: String): Flow<State<String>>
    fun getParkingInvoiceById(id: String): Flow<State<MutableList<ParkingInvoiceFirebase>>>
    fun updateParkingInvoice(parkingInvoice: ParkingInvoiceFirebase): Flow<State<Boolean>>
    fun getUserParkingInvoiceList(): Flow<State<MutableList<ParkingInvoiceFirebase>>>
    fun searchParkingInvoiceUser(licensePlate: String): Flow<State<MutableList<ParkingInvoiceFirebase>>>
    fun searchParkingInvoiceParkingLot(licensePlate: String): Flow<State<MutableList<ParkingInvoiceFirebase>>>
    fun getParkingLotInvoiceList(): Flow<State<MutableList<ParkingInvoiceFirebase>>>
    fun searchParkingInvoiceByLicensePlateAndStateParking(licensePlate: String): Flow<State<Boolean>>
}