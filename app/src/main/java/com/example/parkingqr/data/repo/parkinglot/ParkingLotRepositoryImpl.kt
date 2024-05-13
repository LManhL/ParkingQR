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

    override fun createParkingLot(parkingLot: ParkingLot): Flow<State<String>> {
        return remoteData.createParkingLot(parkingLot.mapToParkingLotFirebase())
    }

    override fun createBillingType(
        parkingLotId: String,
        billingType: BillingType
    ): Flow<State<Boolean>> {
        return remoteData.createBillingType(parkingLotId, billingType.mapToBillingTypeFirebase())
    }

    override fun getAllMonthlyTicketTypeList(parkingLotId: String): Flow<State<List<MonthlyTicketType>>> {
        return remoteData.getAllMonthlyTicketTypeList(parkingLotId).map { state ->
            when (state) {
                is State.Loading -> State.loading()
                is State.Success -> State.success(state.data.map { it.mapToMonthlyTicket() })
                is State.Failed -> State.failed(state.message)
            }
        }
    }

    override fun updateMonthlyTicketType(
        parkingLotId: String,
        monthlyTicketType: MonthlyTicketType
    ): Flow<State<Boolean>> {
        return remoteData.updateMonthlyTicketType(
            parkingLotId,
            monthlyTicketType.mapToMonthlyTicketFirebase()
        )
    }

    override fun deleteMonthlyTicketType(
        parkingLotId: String,
        monthlyTicketType: MonthlyTicketType
    ): Flow<State<Boolean>> {
        return remoteData.deleteMonthlyTicketType(
            parkingLotId,
            monthlyTicketType.mapToMonthlyTicketFirebase()
        )
    }

    override fun acceptParkingLotById(parkingLotId: String): Flow<State<Boolean>> {
        return remoteData.acceptParkingLotById(parkingLotId)
    }

    override fun declineParkingLotById(parkingLotId: String): Flow<State<Boolean>> {
        return remoteData.declineParkingLotById(parkingLotId)
    }

    override fun createMonthlyTicketType(
        parkingLotId: String,
        monthlyTicketType: MonthlyTicketType
    ): Flow<State<Boolean>> {
        return remoteData.createMonthlyTicketType(
            parkingLotId,
            monthlyTicketType.mapToMonthlyTicketFirebase()
        )
    }

    override fun deleteParkingLotById(parkingLotId: String): Flow<State<Boolean>> {
        return remoteData.deleteParkingLotById(parkingLotId)
    }

    override fun searchParkingLotByName(name: String): Flow<State<List<ParkingLot>>> {
        return remoteData.searchParkingLotByName(name).map { state ->
            when (state) {
                is State.Loading -> State.loading()
                is State.Success -> State.success(state.data.map { it.mapToParkingLot() })
                is State.Failed -> State.failed(state.message)
            }
        }
    }
}