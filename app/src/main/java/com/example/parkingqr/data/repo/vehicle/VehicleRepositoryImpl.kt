package com.example.parkingqr.data.repo.vehicle

import com.example.parkingqr.data.local.vehicle.VehicleLocalData
import com.example.parkingqr.data.mapper.mapToParkingInvoice
import com.example.parkingqr.data.mapper.mapToVehicleDetail
import com.example.parkingqr.data.mapper.mapToVehicleFirebase
import com.example.parkingqr.data.remote.State
import com.example.parkingqr.data.remote.vehicle.VehicleRemoteData
import com.example.parkingqr.domain.model.vehicle.VehicleDetail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class VehicleRepositoryImpl @Inject constructor(
    private val vehicleRemoteData: VehicleRemoteData,
    private val vehicleLocalData: VehicleLocalData
) : VehicleRepository {
    override fun createVehicleRegistrationForm(vehicleDetail: VehicleDetail): Flow<State<Boolean>> {
        return vehicleRemoteData.createVehicleRegistrationForm(vehicleDetail.mapToVehicleFirebase())
    }

    override fun getVehicleRegistrationList(): Flow<State<MutableList<VehicleDetail>>> {
        return vehicleRemoteData.getVehicleRegistrationList().map { state ->
            when (state) {
                is State.Loading -> State.loading()
                is State.Success -> State.success(state.data.map { it.mapToVehicleDetail() }
                    .toMutableList())
                is State.Failed -> State.failed(state.message)
            }
        }
    }

    override fun getVehicleById(id: String): Flow<State<VehicleDetail>> {
        return vehicleRemoteData.getVehicleById(id).map { state ->
            when (state) {
                is State.Loading -> State.loading()
                is State.Success -> State.success(state.data.mapToVehicleDetail())
                is State.Failed -> State.failed(state.message)
            }
        }
    }

    override fun deleteVehicleById(id: String): Flow<State<Boolean>> {
        return vehicleRemoteData.deleteVehicleById(id)
    }

    override fun getAllVehicle(): Flow<State<MutableList<VehicleDetail>>> {
        return vehicleRemoteData.getAllVehicle().map { state ->
            when (state) {
                is State.Loading -> State.loading()
                is State.Success -> State.success(state.data.map { it.mapToVehicleDetail() }
                    .toMutableList())
                is State.Failed -> State.failed(state.message)
            }
        }
    }

    override fun acceptVehicle(vehicleDetail: VehicleDetail): Flow<State<Boolean>> {
        return vehicleRemoteData.acceptVehicle(vehicleDetail.mapToVehicleFirebase())
    }

    override fun refuseVehicle(vehicleDetail: VehicleDetail): Flow<State<Boolean>> {
        return vehicleRemoteData.refuseVehicle(vehicleDetail.mapToVehicleFirebase())
    }

    override fun pendingVehicle(vehicleDetail: VehicleDetail): Flow<State<Boolean>> {
        return vehicleRemoteData.pendingVehicle(vehicleDetail.mapToVehicleFirebase())
    }
}