package com.example.parkingqr.data.repo.parkinglot

import com.example.parkingqr.data.remote.State
import com.example.parkingqr.domain.model.parkinglot.ParkingLot
import com.example.parkingqr.domain.model.parkinglot.Rate
import kotlinx.coroutines.flow.Flow

interface ParkingLotRepository {
    fun getParkingLotList(): Flow<State<MutableList<ParkingLot>>>
    fun getParkingLotRates(parkingLotId: String): Flow<State<MutableList<Rate>>>
    fun getParkingLotById(id: String): Flow<State<ParkingLot>>
}