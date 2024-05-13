package com.example.parkingqr.data.repo.invoice

import com.example.parkingqr.data.local.invoice.InvoiceLocalData
import com.example.parkingqr.data.mapper.*
import com.example.parkingqr.data.remote.State
import com.example.parkingqr.data.remote.invoice.InvoiceRemoteData
import com.example.parkingqr.domain.model.invoice.ParkingInvoice
import com.example.parkingqr.domain.model.invoice.WaitingRate
import com.example.parkingqr.domain.model.vehicle.VehicleInvoice
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class InvoiceRepositoryImpl @Inject constructor(
    private val invoiceRemoteData: InvoiceRemoteData,
    private val invoiceLocalData: InvoiceLocalData
) : InvoiceRepository {
    override fun searchLicensePlateByUserId(
        licensePlate: String,
        userId: String
    ): Flow<State<MutableList<VehicleInvoice>>> {
        return invoiceRemoteData.searchLicensePlateByUserId(licensePlate, userId).map { state ->
            when (state) {
                is State.Loading -> State.loading()
                is State.Success -> State.success(state.data.map { it.mapToVehicleInvoice() }
                    .toMutableList())
                is State.Failed -> State.failed(state.message)
            }
        }
    }

    override fun addNewParkingInvoice(parkingInvoice: ParkingInvoice): Flow<State<String>> {
        return invoiceRemoteData.addNewParkingInvoice(parkingInvoice.mapToParkingInvoiceFirebase())
    }

    override fun searchParkingInvoiceById(id: String): Flow<State<ParkingInvoice>> {
        return invoiceRemoteData.searchParkingInvoiceById(id).map { state ->
            when (state) {
                is State.Loading -> State.loading()
                is State.Success -> State.success(state.data.mapToParkingInvoice())
                is State.Failed -> State.failed(state.message)
            }
        }
    }

    override fun getNewParkingInvoiceKey(): String {
        return invoiceRemoteData.getNewParkingInvoiceKey()
    }

    override fun completeParkingInvoice(parkingInvoice: ParkingInvoice): Flow<State<String>> {
        return invoiceRemoteData.completeParkingInvoice(parkingInvoice.mapToParkingInvoiceFirebase())
    }

    override fun refuseParkingInvoice(id: String): Flow<State<String>> {
        return invoiceRemoteData.refuseParkingInvoice(id)
    }

    override fun getParkingInvoiceById(id: String): Flow<State<MutableList<ParkingInvoice>>> {
        return invoiceRemoteData.getParkingInvoiceById(id).map { state ->
            when (state) {
                is State.Loading -> State.loading()
                is State.Success -> State.success(state.data.map { it.mapToParkingInvoice() }
                    .toMutableList())
                is State.Failed -> State.failed(state.message)
            }
        }
    }

    override fun updateParkingInvoice(parkingInvoice: ParkingInvoice): Flow<State<Boolean>> {
        return invoiceRemoteData.updateParkingInvoice(parkingInvoice.mapToParkingInvoiceFirebase())
    }

    override fun getUserParkingInvoiceList(): Flow<State<MutableList<ParkingInvoice>>> {
        return invoiceRemoteData.getUserParkingInvoiceList().map { state ->
            when (state) {
                is State.Loading -> State.loading()
                is State.Success -> State.success(state.data.map { it.mapToParkingInvoice() }
                    .toMutableList())
                is State.Failed -> State.failed(state.message)
            }
        }
    }

    override fun searchParkingInvoiceUser(licensePlate: String): Flow<State<MutableList<ParkingInvoice>>> {
        return invoiceRemoteData.searchParkingInvoiceUser(licensePlate).map { state ->
            when (state) {
                is State.Loading -> State.loading()
                is State.Success -> State.success(state.data.map { it.mapToParkingInvoice() }
                    .toMutableList())
                is State.Failed -> State.failed(state.message)
            }
        }
    }

    override fun searchParkingInvoiceParkingLot(
        licensePlate: String,
        parkingLotId: String
    ): Flow<State<MutableList<ParkingInvoice>>> {
        return invoiceRemoteData.searchParkingInvoiceParkingLot(licensePlate, parkingLotId)
            .map { state ->
                when (state) {
                    is State.Loading -> State.loading()
                    is State.Success -> State.success(state.data.map { it.mapToParkingInvoice() }
                        .toMutableList())
                    is State.Failed -> State.failed(state.message)
                }
            }
    }

    override fun getParkingLotInvoiceList(parkingLotId: String): Flow<State<MutableList<ParkingInvoice>>> {
        return invoiceRemoteData.getParkingLotInvoiceList(parkingLotId).map { state ->
            when (state) {
                is State.Loading -> State.loading()
                is State.Success -> State.success(state.data.map { it.mapToParkingInvoice() }
                    .toMutableList())
                is State.Failed -> State.failed(state.message)
            }
        }
    }

    override fun searchParkingInvoiceByLicensePlateAndStateParking(licensePlate: String): Flow<State<Boolean>> {
        return invoiceRemoteData.searchParkingInvoiceByLicensePlateAndStateParking(licensePlate)
    }

    override fun getUserInvoiceListHaveParkingState(): Flow<State<MutableList<ParkingInvoice>>> {
        return invoiceRemoteData.getUserInvoiceListHaveParkingState().map { state ->
            when (state) {
                is State.Loading -> State.loading()
                is State.Success -> State.success(state.data.map { it.mapToParkingInvoice() }
                    .toMutableList())
                is State.Failed -> State.failed(state.message)
            }

        }
    }

    override fun searchHistoryParkingInvoiceUser(licensePlate: String): Flow<State<MutableList<ParkingInvoice>>> {
        return invoiceRemoteData.searchParkingInvoiceUser(licensePlate)
            .map { state ->
                when (state) {
                    is State.Loading -> State.loading()
                    is State.Success -> State.success(state.data.map { it.mapToParkingInvoice() }
                        .filter { it.state != "parking" }.toMutableList())
                    is State.Failed -> State.failed(state.message)
                }

            }
    }

    override fun updateInvoicePaymentMethod(parkingInvoice: ParkingInvoice): Flow<State<Boolean>> {
        return invoiceRemoteData.updateInvoicePaymentMethod(parkingInvoice.mapToParkingInvoiceFirebase())
            .map { state ->
                when (state) {
                    is State.Loading -> State.loading()
                    is State.Success -> State.success(state.data)
                    is State.Failed -> State.failed(state.message)
                }
            }
    }

    override fun createWaitingRate(parkingInvoice: ParkingInvoice): Flow<State<Boolean>> {
        return invoiceRemoteData.createWaitingRate(parkingInvoice.mapToParkingInvoiceFirebase())
    }

    override fun getUnShowedWaitingRates(): Flow<State<List<WaitingRate>>> {
        return invoiceRemoteData.getUnShowedWaitingRates().map { state ->
            when (state) {
                is State.Loading -> State.loading()
                is State.Success -> State.Success(state.data.map { it.mapToWaitingRate() })
                is State.Failed -> State.failed(state.message)
            }
        }
    }

    override fun updateShowedStateWaitingRate(waitingRate: WaitingRate): Flow<State<Boolean>> {
        return invoiceRemoteData.updateShowedStateWaitingRate(waitingRate.mapToWaitingRateFirebase())
    }

    override fun deleteWaitingRateById(id: String): Flow<State<Boolean>> {
        return invoiceRemoteData.deleteWaitingRateById(id)
    }

    override fun getAllPendingInvoiceByParkingLotId(parkingLotId: String): Flow<State<List<ParkingInvoice>>> {
        return invoiceRemoteData.getAllPendingInvoiceByParkingLotId(parkingLotId).map { state ->
            when (state) {
                is State.Loading -> State.loading()
                is State.Success -> State.success(state.data.map { it.mapToParkingInvoice() }
                    .toMutableList())
                is State.Failed -> State.failed(state.message)
            }
        }
    }

}