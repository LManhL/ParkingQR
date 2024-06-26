package com.example.parkingqr.data.remote.invoice

import com.example.parkingqr.data.remote.State
import com.example.parkingqr.data.remote.dto.invoice.ParkingInvoiceFirebase
import com.example.parkingqr.data.remote.dto.invoice.WaitingRateFirebase
import com.example.parkingqr.data.remote.dto.vehicle.VehicleFirebase
import com.example.parkingqr.domain.model.invoice.ParkingInvoice
import kotlinx.coroutines.flow.Flow

interface InvoiceRemoteData {
    fun searchLicensePlateByUserId(
        licensePlate: String,
        userId: String
    ): Flow<State<MutableList<VehicleFirebase>>>

    fun addNewParkingInvoice(parkingInvoice: ParkingInvoiceFirebase): Flow<State<String>>
    fun searchParkingInvoiceById(id: String): Flow<State<ParkingInvoiceFirebase>>
    fun getNewParkingInvoiceKey(): String
    fun completeParkingInvoice(parkingInvoice: ParkingInvoiceFirebase): Flow<State<String>>
    fun refuseParkingInvoice(id: String): Flow<State<String>>
    fun getParkingInvoiceById(id: String): Flow<State<MutableList<ParkingInvoiceFirebase>>>
    fun updateParkingInvoice(parkingInvoice: ParkingInvoiceFirebase): Flow<State<Boolean>>
    fun getUserParkingInvoiceList(): Flow<State<MutableList<ParkingInvoiceFirebase>>>
    fun searchParkingInvoiceUser(licensePlate: String): Flow<State<MutableList<ParkingInvoiceFirebase>>>
    fun searchParkingInvoiceParkingLot(
        licensePlate: String,
        parkingLotId: String
    ): Flow<State<MutableList<ParkingInvoiceFirebase>>>

    fun getParkingLotInvoiceList(parkingLotId: String): Flow<State<MutableList<ParkingInvoiceFirebase>>>
    fun searchParkingInvoiceByLicensePlateAndStateParking(licensePlate: String): Flow<State<Boolean>>
    fun getUserInvoiceListHaveParkingState(): Flow<State<MutableList<ParkingInvoiceFirebase>>>

    fun updateInvoicePaymentMethod(parkingInvoice: ParkingInvoiceFirebase): Flow<State<Boolean>>
    fun createWaitingRate(parkingInvoice: ParkingInvoiceFirebase): Flow<State<Boolean>>
    fun getUnShowedWaitingRates(): Flow<State<List<WaitingRateFirebase>>>
    fun updateShowedStateWaitingRate(waitingRate: WaitingRateFirebase): Flow<State<Boolean>>
    fun deleteWaitingRateById(id: String): Flow<State<Boolean>>
    fun getAllPendingInvoiceByParkingLotId(parkingLotId: String): Flow<State<List<ParkingInvoiceFirebase>>>

}