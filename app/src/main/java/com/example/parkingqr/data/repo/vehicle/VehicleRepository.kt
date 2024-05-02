package com.example.parkingqr.data.repo.vehicle

import com.example.parkingqr.data.remote.State
import com.example.parkingqr.data.remote.dto.vehicle.VehicleFirebase
import com.example.parkingqr.domain.model.vehicle.VehicleDetail
import com.example.parkingqr.domain.model.vehicle.VehicleInvoice
import kotlinx.coroutines.flow.Flow

interface VehicleRepository {
    fun createVehicleRegistrationForm(vehicleDetail: VehicleDetail): Flow<State<Boolean>>
    fun getVehicleRegistrationList(): Flow<State<MutableList<VehicleDetail>>>
    fun getVehicleById(id: String): Flow<State<VehicleDetail>>
    fun deleteVehicleById(id: String): Flow<State<Boolean>>
    fun getAllVehicle(): Flow<State<MutableList<VehicleDetail>>>
    fun acceptVehicle(vehicleDetail: VehicleDetail): Flow<State<Boolean>>
    fun refuseVehicle(vehicleDetail: VehicleDetail): Flow<State<Boolean>>
    fun pendingVehicle(vehicleDetail: VehicleDetail): Flow<State<Boolean>>

    fun getAllVehicleOfUserByUserId(userId: String): Flow<State<MutableList<VehicleInvoice>>>
    fun getVerifiedVehiclesOfUser(userId: String): Flow<State<List<VehicleInvoice>>>
}