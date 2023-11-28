package com.example.parkingqr.data.remote.model.login

import com.example.parkingqr.data.remote.model.parking.UserResponse
import com.example.parkingqr.data.remote.model.parking.VehicleResponse

data class ParkingInvoiceResponse (
    val id: String? = null,
    val user: UserResponse? = null,
    val vehicleResponse: VehicleResponse? = null,
    val state: String? = null,
    val imageIn: String? = null,
    val imageOut: String? = null,
    val price: Double? = null,
    val timeIn: String? = null,
    val timeOut: String? = null,
    val paymentMethod: String? = null,
    val type: String? = null,
    val note: String? = null
)