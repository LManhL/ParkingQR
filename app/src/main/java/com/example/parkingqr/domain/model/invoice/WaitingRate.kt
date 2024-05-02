package com.example.parkingqr.domain.model.invoice

import com.example.parkingqr.domain.model.parkinglot.UserRate
import com.example.parkingqr.domain.model.vehicle.VehicleInvoice

class WaitingRate(
    var id: String = "",
    var parkingLotId: String = "",
    val parkingInvoiceId: String = "",
    var rate: Double = 0.0,
    var comment: String = "",
    var createAt: String = "",
    var user: UserRate = UserRate(),
    val showed: Boolean = false,
    var licensePlate: String = "",
    var brand: String = "",
    var vehicleType: String = "",
) {
    fun getReadableVehicleType(): String {
        return if (vehicleType == VehicleInvoice.CAR_TYPE) {
            "Xe hơi"
        } else if (vehicleType == VehicleInvoice.MOTORBIKE_TYPE) {
            "Xe máy"
        } else "Khác"
    }
}