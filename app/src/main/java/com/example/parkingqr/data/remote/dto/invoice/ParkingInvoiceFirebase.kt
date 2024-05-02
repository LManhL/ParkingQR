package com.example.parkingqr.data.remote.dto.invoice

import com.example.parkingqr.domain.model.invoice.ParkingInvoice
import com.example.parkingqr.domain.model.invoice.UserInvoice
import com.example.parkingqr.domain.model.vehicle.VehicleInvoice

data class ParkingInvoiceFirebase(
    var id: String? = null,
    var parkingLotId: String? = null,
    var user: UserInvoiceFirebase? = null,
    var vehicle: VehicleInvoiceFirebase? = null,
    var state: String? = null,
    var imageIn: String? = null,
    var imageOut: String? = null,
    var price: Double? = null,
    var timeIn: String? = null,
    var timeOut: String? = null,
    var paymentMethod: String? = null,
    var type: String? = null,
    var note: String? = null
)