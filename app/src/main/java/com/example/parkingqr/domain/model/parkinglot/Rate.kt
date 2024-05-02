package com.example.parkingqr.domain.model.parkinglot

import com.example.parkingqr.domain.model.vehicle.VehicleInvoice

class Rate(
    var id: String = "",
    var parkingLotId: String = "",
    val parkingInvoiceId: String = "",
    var rate: Double = 0.0,
    var comment: String = "",
    var createAt: String = "",
    var userRate: UserRate = UserRate(),
    var licensePlate: String = "",
    var brand: String = "",
    var vehicleType: String = "",
){
    fun getReadableVehicleType(): String {
        return if (vehicleType == VehicleInvoice.CAR_TYPE) {
            "Xe hơi"
        } else if (vehicleType == VehicleInvoice.MOTORBIKE_TYPE) {
            "Xe máy"
        } else "Khác"
    }
}

class UserRate(
    var userId: String = "",
    var name: String = "",
    var avatar: String = ""
)