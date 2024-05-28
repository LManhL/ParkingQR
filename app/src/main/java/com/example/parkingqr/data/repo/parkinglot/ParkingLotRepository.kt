package com.example.parkingqr.data.repo.parkinglot

import com.example.parkingqr.data.remote.State
import com.example.parkingqr.data.remote.dto.parkinglot.SecurityCameraFirebase
import com.example.parkingqr.domain.model.invoice.WaitingRate
import com.example.parkingqr.domain.model.parkinglot.*
import kotlinx.coroutines.flow.Flow

interface ParkingLotRepository {
    fun getParkingLotList(): Flow<State<MutableList<ParkingLot>>>
    fun getParkingLotRates(parkingLotId: String): Flow<State<MutableList<Rate>>>
    fun getParkingLotById(id: String): Flow<State<ParkingLot>>
    fun getBillingTypesByParkingLotId(parkingLotId: String): Flow<State<MutableList<BillingType>>>
    fun updateBillingType(parkingLotId: String, billingType: BillingType): Flow<State<Boolean>>
    fun getMonthlyTicketByVehicleType(
        parkingLotId: String,
        vehicleType: String
    ): Flow<State<MutableList<MonthlyTicketType>>>

    fun createRate(waitingRate: WaitingRate): Flow<State<Boolean>>
    fun createParkingLot(parkingLot: ParkingLot): Flow<State<String>>
    fun createBillingType(parkingLotId: String, billingType: BillingType): Flow<State<Boolean>>

    fun getAllMonthlyTicketTypeList(parkingLotId: String): Flow<State<List<MonthlyTicketType>>>

    fun updateMonthlyTicketType(
        parkingLotId: String,
        monthlyTicketType: MonthlyTicketType
    ): Flow<State<Boolean>>

    fun deleteMonthlyTicketType(
        parkingLotId: String,
        monthlyTicketType: MonthlyTicketType
    ): Flow<State<Boolean>>

    fun createMonthlyTicketType(
        parkingLotId: String,
        monthlyTicketType: MonthlyTicketType
    ): Flow<State<Boolean>>

    fun acceptParkingLotById(parkingLotId: String): Flow<State<Boolean>>
    fun declineParkingLotById(parkingLotId: String): Flow<State<Boolean>>
    fun deleteParkingLotById(parkingLotId: String): Flow<State<Boolean>>

    fun searchParkingLotByName(name: String): Flow<State<List<ParkingLot>>>
    fun setUriCameraIn(uri: String)
    fun setUriCameraOut(uri: String)
    fun getUriCameraIn(): String?
    fun getUriCameraOut(): String?
    fun addCamera(
        parkingLotId: String,
        securityCamera: SecurityCamera
    ): Flow<State<Boolean>>

    fun updateCamera(
        parkingLotId: String,
        securityCamera: SecurityCamera
    ): Flow<State<Boolean>>

    fun deleteCameraById(parkingLotId: String, securityCameraId: String): Flow<State<Boolean>>
    fun getCameraIn(parkingLotId: String): Flow<State<SecurityCamera>>
    fun getCameraOut(parkingLotId: String): Flow<State<SecurityCamera>>
    fun getAllCameras(parkingLotId: String): Flow<State<List<SecurityCamera>>>
}