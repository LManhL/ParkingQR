package com.example.parkingqr.data.repo.invoice

import com.example.parkingqr.data.remote.State
import com.example.parkingqr.data.remote.dto.invoice.ParkingInvoiceFirebase
import com.example.parkingqr.data.remote.dto.user.ParkingLotManagerFirebase
import com.example.parkingqr.domain.model.invoice.ParkingInvoice
import com.example.parkingqr.domain.model.invoice.WaitingRate
import com.example.parkingqr.domain.model.vehicle.VehicleInvoice
import kotlinx.coroutines.flow.Flow

interface InvoiceRepository {
    fun searchLicensePlateByUserId(licensePlate: String, userId: String): Flow<State<MutableList<VehicleInvoice>>>
    fun addNewParkingInvoice(parkingInvoice: ParkingInvoice): Flow<State<String>>
    fun searchParkingInvoiceById(id: String): Flow<State<ParkingInvoice>>
    fun getNewParkingInvoiceKey(): String
    fun completeParkingInvoice(parkingInvoice: ParkingInvoice): Flow<State<String>>
    fun refuseParkingInvoice(id: String): Flow<State<String>>
    fun getParkingInvoiceById(id: String): Flow<State<MutableList<ParkingInvoice>>>
    fun updateParkingInvoice(parkingInvoice: ParkingInvoice): Flow<State<Boolean>>
    fun getUserParkingInvoiceList(): Flow<State<MutableList<ParkingInvoice>>>
    fun searchParkingInvoiceUser(licensePlate: String): Flow<State<MutableList<ParkingInvoice>>>
    fun searchParkingInvoiceParkingLot(licensePlate: String, parkingLotId: String): Flow<State<MutableList<ParkingInvoice>>>
    fun getParkingLotInvoiceList(parkingLotId: String): Flow<State<MutableList<ParkingInvoice>>>
    fun searchParkingInvoiceByLicensePlateAndStateParking(licensePlate: String): Flow<State<Boolean>>

    fun getUserInvoiceListHaveParkingState(): Flow<State<MutableList<ParkingInvoice>>>

    fun searchHistoryParkingInvoiceUser(licensePlate: String): Flow<State<MutableList<ParkingInvoice>>>

    fun updateInvoicePaymentMethod(parkingInvoice: ParkingInvoice): Flow<State<Boolean>>
    fun createWaitingRate(parkingInvoice: ParkingInvoice): Flow<State<Boolean>>
    fun getUnShowedWaitingRates(): Flow<State<List<WaitingRate>>>
    fun updateShowedStateWaitingRate(waitingRate: WaitingRate): Flow<State<Boolean>>
    fun deleteWaitingRateById(id: String): Flow<State<Boolean>>
}