package com.example.parkingqr.domain.model.parkinglot

import com.example.parkingqr.domain.model.vehicle.VehicleDetail

class MonthlyTicketType(
    var id: String = DRAFT_ID_ITEM,
    var numberOfMonth: Double = 0.0,
    var originalPrice: Double = 0.0,
    var promotionalPrice: Double = 0.0,
    var description: String = "",
    var vehicleType: String = ""
) {
    companion object {
        const val DRAFT_ID_ITEM = "-1"

        fun createMonthlyTicketTypeByVehicleType(vehicleType: String): MonthlyTicketType {
            return MonthlyTicketType(
                vehicleType = vehicleType
            )
        }


    }

    fun getReadableVehicleType(): String {
        return if (vehicleType == VehicleDetail.CAR_TYPE) {
            "Xe hơi"
        } else if (vehicleType == VehicleDetail.MOTORBIKE_TYPE) {
            "Xe máy"
        } else "Khác"
    }
}