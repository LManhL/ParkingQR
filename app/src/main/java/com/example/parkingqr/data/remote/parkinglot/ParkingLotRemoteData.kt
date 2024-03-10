package com.example.parkingqr.data.remote.parkinglot

import com.example.parkingqr.data.remote.State
import com.example.parkingqr.data.remote.dto.parkinglot.ParkingLotFirebase
import com.example.parkingqr.data.remote.dto.parkinglot.RateFirebase
import kotlinx.coroutines.flow.Flow

interface ParkingLotRemoteData {
    fun getParkingLotList(): Flow<State<MutableList<ParkingLotFirebase>>>
    fun getParkingLotById(id: String): Flow<State<ParkingLotFirebase>>
    fun getParkingLotRates(parkingLotId: String): Flow<State<MutableList<RateFirebase>>>
}