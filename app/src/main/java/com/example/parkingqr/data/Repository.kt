package com.example.parkingqr.data

import com.example.parkingqr.data.remote.RemoteDataSource
import com.example.parkingqr.data.remote.State
import com.example.parkingqr.domain.invoice.ParkingInvoiceIV
import com.example.parkingqr.domain.parking.ParkingInvoicePK
import com.example.parkingqr.domain.parking.User
import com.example.parkingqr.domain.parking.Vehicle
import kotlinx.coroutines.flow.Flow

class Repository: IRepository {
    private val remoteDataSource = RemoteDataSource()

    override fun searchLicensePlate(licensePlate: String): Flow<State<MutableList<Vehicle>>> {
        return remoteDataSource.searchLicensePlate(licensePlate)
    }

    override fun searchUserById(userId: String): Flow<State<MutableList<User>>> {
        return remoteDataSource.searchUserById(userId)
    }

    override fun addNewParkingInvoice(parkingInvoicePK: ParkingInvoicePK): Flow<State<String>> {
        return remoteDataSource.addNewParkingInvoice(parkingInvoicePK)
    }

    override fun searchParkingInvoiceById(id: String): Flow<State<MutableList<ParkingInvoicePK>>> {
        return remoteDataSource.searchParkingInvoiceById(id)
    }

    override fun getNewParkingInvoiceKey(): String {
        return remoteDataSource.getNewParkingInvoiceKey()
    }

    override fun completeParkingInvoice(id: String): Flow<State<String>> {
        return remoteDataSource.completeParkingInvoice(id)
    }

    override fun searchParkingInvoiceByLicensePlateAndStateParking(licensePlate: String): Flow<State<Boolean>> {
        return remoteDataSource.searchParkingInvoiceByLicensePlateAndStateParking(licensePlate)
    }

    override fun getParkingInvoiceList(id: String): Flow<State<MutableList<ParkingInvoiceIV>>> {
        return remoteDataSource.getParkingInvoiceList(id)
    }
}