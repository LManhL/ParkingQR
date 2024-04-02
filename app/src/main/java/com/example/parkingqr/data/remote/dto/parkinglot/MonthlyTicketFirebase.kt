package com.example.parkingqr.data.remote.dto.parkinglot

import com.example.parkingqr.data.remote.dto.user.UserFirebase
import com.example.parkingqr.data.remote.dto.vehicle.VehicleFirebase

class MonthlyTicketFirebase(
    var id: String? = null,
    val parkingLot: ParkingLotFirebase? = null,
    val createAt: String? = null,
    val expiredAt: String? = null,
    val vehicle: VehicleFirebase? = null,
    val user: UserFirebase? = null,
    val monthlyTicketType: MonthlyTicketTypeFirebase? = null
)