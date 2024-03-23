package com.example.parkingqr.data.remote.vehicle

import com.example.parkingqr.data.remote.State
import com.example.parkingqr.data.remote.dto.vehicle.VehicleFirebase
import com.example.parkingqr.domain.model.vehicle.VehicleDetail
import kotlinx.coroutines.flow.Flow

interface VehicleRemoteData {
    fun createVehicleRegistrationForm(vehicle: VehicleFirebase): Flow<State<Boolean>>
    fun getVehicleRegistrationList(): Flow<State<MutableList<VehicleFirebase>>>
    fun getVehicleById(id: String): Flow<State<VehicleFirebase>>
    fun deleteVehicleById(id: String): Flow<State<Boolean>>
    fun getAllVehicle(): Flow<State<MutableList<VehicleFirebase>>>
    fun acceptVehicle(vehicle: VehicleFirebase): Flow<State<Boolean>>
    fun refuseVehicle(vehicle: VehicleFirebase): Flow<State<Boolean>>
    fun pendingVehicle(vehicle: VehicleFirebase): Flow<State<Boolean>>

    fun getAllVehicleOfUserByUserId(userId: String): Flow<State<MutableList<VehicleFirebase>>>
}