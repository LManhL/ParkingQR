package com.example.parkingqr.data.repo.invoice

import com.example.parkingqr.data.local.invoice.InvoiceLocalData
import com.example.parkingqr.data.mapper.mapToParkingInvoice
import com.example.parkingqr.data.mapper.mapToParkingInvoiceFirebase
import com.example.parkingqr.data.mapper.mapToVehicleInvoice
import com.example.parkingqr.data.remote.State
import com.example.parkingqr.data.remote.invoice.InvoiceRemoteData
import com.example.parkingqr.domain.model.invoice.ParkingInvoice
import com.example.parkingqr.domain.model.vehicle.VehicleInvoice
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class InvoiceRepositoryImpl @Inject constructor(
    private val invoiceRemoteData: InvoiceRemoteData,
    private val invoiceLocalData: InvoiceLocalData
) : InvoiceRepository {
    override fun searchLicensePlate(licensePlate: String): Flow<State<MutableList<VehicleInvoice>>> {
        return invoiceRemoteData.searchLicensePlate(licensePlate).map { state ->
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

    override fun searchParkingInvoiceById(id: String): Flow<State<MutableList<ParkingInvoice>>> {
        return invoiceRemoteData.searchParkingInvoiceById(id).map { state ->
            when (state) {
                is State.Loading -> State.loading()
                is State.Success -> State.success(state.data.map { it.mapToParkingInvoice() }
                    .toMutableList())
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

    override fun searchParkingInvoiceParkingLot(licensePlate: String): Flow<State<MutableList<ParkingInvoice>>> {
        return invoiceRemoteData.searchParkingInvoiceParkingLot(licensePlate).map { state ->
            when (state) {
                is State.Loading -> State.loading()
                is State.Success -> State.success(state.data.map { it.mapToParkingInvoice() }
                    .toMutableList())
                is State.Failed -> State.failed(state.message)
            }
        }
    }

    override fun getParkingLotInvoiceList(): Flow<State<MutableList<ParkingInvoice>>> {
        return invoiceRemoteData.getParkingLotInvoiceList().map { state ->
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
}