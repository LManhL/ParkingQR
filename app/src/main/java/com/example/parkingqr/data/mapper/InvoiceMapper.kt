package com.example.parkingqr.data.mapper

import com.example.parkingqr.data.remote.dto.invoice.ParkingInvoiceFirebase
import com.example.parkingqr.data.remote.dto.invoice.UserInvoiceFirebase
import com.example.parkingqr.data.remote.dto.invoice.VehicleInvoiceFirebase
import com.example.parkingqr.domain.model.invoice.ParkingInvoice
import com.example.parkingqr.domain.model.invoice.UserInvoice
import com.example.parkingqr.domain.model.vehicle.VehicleInvoice

fun ParkingInvoice.mapToParkingInvoiceFirebase(): ParkingInvoiceFirebase {
    return ParkingInvoiceFirebase(
        id = id,
        imageIn = imageIn,
        state = state,
        imageOut = imageOut,
        price = price,
        paymentMethod = paymentMethod,
        type = type,
        note = note,
        timeIn = timeIn,
        timeOut = timeOut,
        user = user.mapToUserInvoiceFirebase(),
        vehicle = vehicle.mapToVehicleInvoiceFirebase(),
        parkingLotId = parkingLotId,
    )
}

fun ParkingInvoiceFirebase.mapToParkingInvoice(): ParkingInvoice {
    return ParkingInvoice(
        id = id ?: "",
        user = user?.mapToUserInvoice() ?: UserInvoice(),
        vehicle = vehicle?.mapToVehicleInvoice() ?: VehicleInvoice(),
        state = state ?: "",
        imageIn = imageIn ?: "",
        imageOut = imageOut ?: "",
        price = price ?: 0.0,
        timeIn = timeIn ?: "",
        timeOut = timeOut ?: "",
        paymentMethod = paymentMethod ?: "",
        type = type ?: "",
        note = note ?: "",
        parkingLotId = parkingLotId ?: "",
    )
}

fun UserInvoice.mapToUserInvoiceFirebase(): UserInvoiceFirebase {
    return UserInvoiceFirebase(
        id = id ?: "",
        userId = userId ?: "",
        name = name ?: "",
        phoneNumber = phoneNumber ?: ""
    )
}

fun VehicleInvoice.mapToVehicleInvoiceFirebase(): VehicleInvoiceFirebase {
    return VehicleInvoiceFirebase(
        id = id,
        userId = userId,
        licensePlate = licensePlate,
        state = state,
        brand = brand,
        type = type,
        color = color,
        ownerFullName = ownerFullName
    )
}

fun UserInvoiceFirebase.mapToUserInvoice(): UserInvoice {
    return UserInvoice(
        id = id ?: "",
        userId = userId ?: "",
        name = name ?: "",
        phoneNumber = phoneNumber ?: ""
    )
}

fun VehicleInvoiceFirebase.mapToVehicleInvoice(): VehicleInvoice {
    return VehicleInvoice(
        id = id ?: "",
        userId = userId ?: "",
        licensePlate = licensePlate ?: "",
        state = state ?: "",
        brand = brand ?: "",
        type = type ?: "",
        color = color ?: "",
        ownerFullName = ownerFullName ?: "",
    )
}


