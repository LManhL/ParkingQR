package com.example.parkingqr.data.repo.parkinglot

import com.example.parkingqr.data.mapper.*
import com.example.parkingqr.data.remote.State
import com.example.parkingqr.data.remote.parkinglot.ParkingLotRemoteData
import com.example.parkingqr.domain.model.invoice.WaitingRate
import com.example.parkingqr.domain.model.parkinglot.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ParkingLotRepositoryImpl @Inject constructor(
    private val remoteData: ParkingLotRemoteData
) : ParkingLotRepository {
    override fun getParkingLotList(): Flow<State<MutableList<ParkingLot>>> {
        return remoteData.getParkingLotList().map { state ->
            when (state) {
                is State.Loading -> State.loading()
                is State.Success -> State.success(state.data.map { it.mapToParkingLot() }
                    .toMutableList())
                is State.Failed -> State.failed(state.message)
            }
        }
    }

    override fun getParkingLotRates(parkingLotId: String): Flow<State<MutableList<Rate>>> {
        return remoteData.getParkingLotRates(parkingLotId).map { state ->
            when (state) {
                is State.Loading -> State.loading()
                is State.Success -> State.success(state.data.map { it.mapToWaitingRate() }
                    .toMutableList())
                is State.Failed -> State.failed(state.message)
            }
        }
    }

    override fun getParkingLotById(id: String): Flow<State<ParkingLot>> {
        return remoteData.getParkingLotById(id).map { state ->
            when (state) {
                is State.Loading -> State.loading()
                is State.Success -> State.success(state.data.mapToParkingLot())
                is State.Failed -> State.failed(state.message)
            }
        }
    }

    override fun getBillingTypesByParkingLotId(parkingLotId: String): Flow<State<MutableList<BillingType>>> {
        return remoteData.getBillingTypesByParkingLotId(parkingLotId).map { state ->
            when (state) {
                is State.Loading -> State.loading()
                is State.Success -> State.success(state.data.map {
                    it.mapToBillingType()
                }.toMutableList())
                is State.Failed -> State.failed(state.message)
            }
        }
    }

    override fun updateBillingType(
        parkingLotId: String,
        billingType: BillingType
    ): Flow<State<Boolean>> {
        return remoteData.updateBillingType(parkingLotId, billingType.mapToBillingTypeFirebase())
            .map { state ->
                when (state) {
                    is State.Loading -> State.loading()
                    is State.Success -> State.success(state.data)
                    is State.Failed -> State.failed(state.message)
                }
            }
    }

    override fun getMonthlyTicketByVehicleType(
        parkingLotId: String,
        vehicleType: String
    ): Flow<State<MutableList<MonthlyTicketType>>> {
        return remoteData.getMonthlyTicketList(parkingLotId, vehicleType).map { state ->
            when (state) {
                is State.Loading -> State.loading()
                is State.Success -> State.success(state.data.map { it.mapToMonthlyTicket() }
                    .toMutableList())
                is State.Failed -> State.failed(state.message)
            }
        }
    }

    override fun createRate(waitingRate: WaitingRate): Flow<State<Boolean>> {
        return remoteData.createRate(waitingRate.mapToWaitingRateFirebase())
    }

}