package com.example.parkingqr.data.remote.parkinglot

import com.example.parkingqr.data.remote.State
import com.example.parkingqr.data.remote.dto.invoice.WaitingRateFirebase
import com.example.parkingqr.data.remote.dto.parkinglot.*
import com.example.parkingqr.domain.model.parkinglot.ParkingLot
import kotlinx.coroutines.flow.Flow

interface ParkingLotRemoteData {
    fun getParkingLotList(): Flow<State<MutableList<ParkingLotFirebase>>>
    fun getParkingLotById(id: String): Flow<State<ParkingLotFirebase>>
    fun getParkingLotRates(parkingLotId: String): Flow<State<MutableList<RateFirebase>>>
    fun getBillingTypesByParkingLotId(parkingLotId: String): Flow<State<MutableList<BillingTypeFirebase>>>
    fun updateBillingType(
        parkingLotId: String,
        billingTypeFirebase: BillingTypeFirebase
    ): Flow<State<Boolean>>

    fun getMonthlyTicketList(
        parkingLotId: String,
        vehicleType: String
    ): Flow<State<MutableList<MonthlyTicketTypeFirebase>>>

    fun createRate(waitingRateFirebase: WaitingRateFirebase): Flow<State<Boolean>>
    fun createParkingLot(parkingLot: ParkingLotFirebase): Flow<State<String>>

    fun createBillingType(
        parkingLotId: String,
        billingTypeFirebase: BillingTypeFirebase
    ): Flow<State<Boolean>>

    fun getAllMonthlyTicketTypeList(parkingLotId: String): Flow<State<List<MonthlyTicketTypeFirebase>>>
    fun updateMonthlyTicketType(
        parkingLotId: String,
        monthlyTicketTypeFirebase: MonthlyTicketTypeFirebase
    ): Flow<State<Boolean>>

    fun deleteMonthlyTicketType(
        parkingLotId: String,
        monthlyTicketTypeFirebase: MonthlyTicketTypeFirebase
    ): Flow<State<Boolean>>

    fun createMonthlyTicketType(
        parkingLotId: String,
        monthlyTicketTypeFirebase: MonthlyTicketTypeFirebase
    ): Flow<State<Boolean>>

    fun acceptParkingLotById(parkingLotId: String): Flow<State<Boolean>>
    fun declineParkingLotById(parkingLotId: String): Flow<State<Boolean>>
    fun deleteParkingLotById(parkingLotId: String): Flow<State<Boolean>>
    fun searchParkingLotByName(name: String): Flow<State<List<ParkingLotFirebase>>>
    fun addCamera(
        parkingLotId: String,
        securityCameraFirebase: SecurityCameraFirebase
    ): Flow<State<Boolean>>

    fun updateCamera(
        parkingLotId: String,
        securityCameraFirebase: SecurityCameraFirebase
    ): Flow<State<Boolean>>

    fun deleteCameraById(parkingLotId: String, securityCameraId: String): Flow<State<Boolean>>
    fun getCameraIn(parkingLotId: String): Flow<State<SecurityCameraFirebase>>
    fun getCameraOut(parkingLotId: String): Flow<State<SecurityCameraFirebase>>
    fun getAllCameras(parkingLotId: String): Flow<State<List<SecurityCameraFirebase>>>
}