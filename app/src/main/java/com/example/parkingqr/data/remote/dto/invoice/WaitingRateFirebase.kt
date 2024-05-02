package com.example.parkingqr.data.remote.dto.invoice

import com.example.parkingqr.data.remote.dto.parkinglot.UserRateFirebase

data class WaitingRateFirebase(
    val id: String? = null,
    val parkingLotId: String? = null,
    val parkingInvoiceId: String? = null,
    val rate: Double? = null,
    val comment: String? = null,
    val createAt: String? = null,
    val user: UserRateFirebase? = null,
    var licensePlate: String? = null,
    var brand: String? = null,
    var vehicleType: String? = null,
    val showed: Boolean? = null,
)