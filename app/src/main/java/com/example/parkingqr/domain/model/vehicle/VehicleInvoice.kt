package com.example.parkingqr.domain.model.vehicle

import com.example.parkingqr.data.remote.dto.invoice.VehicleInvoiceFirebase
import com.example.parkingqr.data.remote.dto.vehicle.VehicleFirebase

class VehicleInvoice() {

    companion object {
        const val CAR_TYPE = "car"
        const val MOTORBIKE_TYPE = "motorbike"
        const val OTHER_TYPE = "other"
    }

    var id: String = ""
    var userId: String = ""
    var licensePlate: String = ""
    var state: String = ""
    var brand: String = ""
    var type: String = ""
    var color: String = ""
    var ownerFullName: String = ""

    constructor(vehicleResponseFirebase: VehicleFirebase) : this() {
        this.id = vehicleResponseFirebase.id ?: ""
        this.userId = vehicleResponseFirebase.userId ?: ""
        this.licensePlate = vehicleResponseFirebase.licensePlate ?: ""
        this.state = vehicleResponseFirebase.state ?: ""
        this.brand = vehicleResponseFirebase.brand ?: ""
        this.type = vehicleResponseFirebase.type ?: ""
        this.color = vehicleResponseFirebase.color ?: ""
        this.ownerFullName = vehicleResponseFirebase.ownerFullName ?: ""
    }

    constructor(vehicleInvoiceFirebase: VehicleInvoiceFirebase) : this() {
        this.id = vehicleInvoiceFirebase.id ?: ""
        this.userId = vehicleInvoiceFirebase.userId ?: ""
        this.licensePlate = vehicleInvoiceFirebase.licensePlate ?: ""
        this.state = vehicleInvoiceFirebase.state ?: ""
        this.brand = vehicleInvoiceFirebase.brand ?: ""
        this.type = vehicleInvoiceFirebase.type ?: ""
        this.color = vehicleInvoiceFirebase.color ?: ""
        this.ownerFullName = vehicleInvoiceFirebase.ownerFullName ?: ""
    }

    constructor(
        id: String,
        userId: String,
        licensePlate: String,
        state: String,
        brand: String,
        type: String,
        color: String,
        ownerFullName: String
    ) : this() {
        this.id = id
        this.userId = userId
        this.licensePlate = licensePlate
        this.state = state
        this.brand = brand
        this.type = type
        this.color = color
        this.ownerFullName = ownerFullName
    }

    constructor(licensePlate: String) : this() {
        this.licensePlate = licensePlate
    }

    fun getVehicleType(): String {
        return if (type == CAR_TYPE) {
            "Xe hơi"
        } else if (type == MOTORBIKE_TYPE) {
            "Xe máy"
        } else "Khác"
    }
}