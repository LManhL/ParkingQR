package com.example.parkingqr.data.mapper

import com.example.parkingqr.data.remote.dto.vehicle.VehicleFirebase
import com.example.parkingqr.domain.model.vehicle.VehicleDetail
import com.example.parkingqr.domain.model.vehicle.VehicleInvoice

fun VehicleFirebase.mapToVehicleInvoice(): VehicleInvoice {
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

fun VehicleFirebase.mapToVehicleDetail(): VehicleDetail {
    return VehicleDetail(
        id = id,
        createAt = createAt,
        userId = userId,
        licensePlate = licensePlate,
        state = state,
        brand = brand,
        type = type,
        color = color,
        registrationDate = registrationDate,
        expireDate = expireDate,
        chassisNumber = chassisNumber,
        engineNumber = engineNumber,
        ownerFullName = ownerFullName,
        address = address,
        certificateNumber = certificateNumber,
        images = images
    )
}

fun VehicleDetail.mapToVehicleFirebase(): VehicleFirebase {
    return VehicleFirebase(
        id = id,
        createAt = createAt,
        userId = userId,
        licensePlate = licensePlate,
        state = state,
        brand = brand,
        type = type,
        color = color,
        registrationDate = registrationDate,
        expireDate = expireDate,
        chassisNumber = chassisNumber,
        engineNumber = engineNumber,
        ownerFullName = ownerFullName,
        address = address,
        certificateNumber = certificateNumber,
        images = images
    )
}