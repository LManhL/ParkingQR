package com.example.parkingqr.data

import com.example.parkingqr.data.remote.RemoteDataSource
import com.example.parkingqr.data.remote.State
import com.example.parkingqr.domain.parking.ParkingInvoice
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

    override fun addNewParkingInvoice(parkingInvoice: ParkingInvoice): Flow<State<String>> {
        return remoteDataSource.addNewParkingInvoice(parkingInvoice)
    }

    override fun searchParkingInvoiceById(id: String): Flow<State<MutableList<ParkingInvoice>>> {
        return remoteDataSource.searchParkingInvoiceById(id)
    }

    override fun getNewParkingInvoiceKey(): String {
        return remoteDataSource.getNewParkingInvoiceKey()
    }

    override fun completeParkingInvoice(id: String): Flow<State<String>> {
        return remoteDataSource.completeParkingInvoice(id)
    }
}