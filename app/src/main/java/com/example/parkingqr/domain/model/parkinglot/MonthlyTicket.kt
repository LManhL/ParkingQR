package com.example.parkingqr.domain.model.parkinglot

import com.example.parkingqr.domain.model.user.User
import com.example.parkingqr.domain.model.vehicle.VehicleDetail

class MonthlyTicket(
    var id: String = "",
    var parkingLot: ParkingLot = ParkingLot(),
    var createAt: String = "",
    var expiredAt: String = "",
    var vehicle: VehicleDetail = VehicleDetail(),
    var user: User = User(),
    var monthTicketType: MonthlyTicketType = MonthlyTicketType()
)