package com.example.parkingqr.data.mapper

import com.example.parkingqr.data.remote.dto.parkinglot.MonthlyTicketFirebase
import com.example.parkingqr.domain.model.parkinglot.MonthlyTicket
import com.example.parkingqr.domain.model.parkinglot.MonthlyTicketType
import com.example.parkingqr.domain.model.parkinglot.ParkingLot
import com.example.parkingqr.domain.model.user.User
import com.example.parkingqr.domain.model.vehicle.VehicleDetail

fun MonthlyTicket.mapToMonthlyTicketFirebase(): MonthlyTicketFirebase {
    return MonthlyTicketFirebase(
        id = id,
        parkingLot = parkingLot.mapToParkingLotFirebase(),
        createAt = createAt,
        expiredAt = expiredAt,
        vehicle = vehicle.mapToVehicleFirebase(),
        user = user.mapToUserFirebase(),
        monthlyTicketType = monthTicketType.mapToMonthlyTicketFirebase()
    )
}

fun MonthlyTicketFirebase.mapToMonthlyTicket(): MonthlyTicket {
    return MonthlyTicket(
        id = id ?: "",
        parkingLot = parkingLot?.mapToParkingLot() ?: ParkingLot(),
        createAt = createAt ?: "",
        expiredAt = expiredAt ?: "",
        vehicle = vehicle?.mapToVehicleDetail() ?: VehicleDetail(),
        user = user?.mapToUser() ?: User(),
        monthTicketType = monthlyTicketType?.mapToMonthlyTicket() ?: MonthlyTicketType()
    )
}