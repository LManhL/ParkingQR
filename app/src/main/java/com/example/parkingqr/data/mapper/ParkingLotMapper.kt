package com.example.parkingqr.data.mapper

import com.example.parkingqr.data.remote.dto.parkinglot.*
import com.example.parkingqr.domain.model.parkinglot.*
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint

fun ParkingLotFirebase.mapToParkingLot(): ParkingLot {
    return ParkingLot(
        id = id ?: "",
        name = name ?: "",
        location = LatLng(location?.latitude ?: 0.0, location?.longitude ?: 0.0),
        phoneNumber = phoneNumber ?: "",
        capacity = capacity ?: "",
        description = description ?: "",
        address = address ?: "",
        area = area ?: 0.0,
    )
}

fun ParkingLot.mapToParkingLotFirebase(): ParkingLotFirebase {
    return ParkingLotFirebase(
        id = id,
        name = name,
        location = GeoPoint(location.latitude, location.longitude),
        phoneNumber = phoneNumber,
        capacity = capacity,
        description = description,
        address = address,
        area = area,
    )
}

fun RateFirebase.mapToRate(): Rate {
    return Rate(
        id = id ?: "",
        parkingLotId = parkingLotId ?: "",
        rate = rate ?: 0.0,
        comment = comment ?: "",
        createAt = createAt ?: "",
        userRate = user?.mapToUserRate() ?: UserRate()
    )
}

fun UserRateFirebase.mapToUserRate(): UserRate {
    return UserRate(
        userId = userId ?: "",
        name = name ?: "",
        avatar = avatar ?: ""
    )
}

fun BillingTypeFirebase.mapToBillingType(): BillingType {
    return BillingType(
        id = id ?: "",
        firstBlockPrice = firstBlockPrice ?: 0.0,
        afterFirstBlockPrice = afterFirstBlockPrice ?: 0.0,
        firstBlock = firstBlock ?: 0.0,
        roundedMinutesToOneHour = roundedMinutesToOneHour ?: 0.0,
        nightSurcharge = nightSurcharge ?: 0.0,
        startDaylightTime = startDaylightTime ?: "",
        endDaylightTime = endDaylightTime ?: "",
        startNightTime = startNightTime ?: "",
        endNightTime = endNightTime ?: "",
        type = type ?: "",
        vehicleType = vehicleType ?: "",
        surcharge = surcharge ?: 0.0
    )
}

fun BillingType.mapToBillingTypeFirebase(): BillingTypeFirebase {
    return BillingTypeFirebase(
        id = id,
        firstBlockPrice = firstBlockPrice,
        afterFirstBlockPrice = afterFirstBlockPrice,
        firstBlock = firstBlock,
        roundedMinutesToOneHour = roundedMinutesToOneHour,
        nightSurcharge = nightSurcharge,
        startDaylightTime = startDaylightTime,
        endDaylightTime = endDaylightTime,
        startNightTime = startNightTime,
        endNightTime = endNightTime,
        type = type,
        vehicleType = vehicleType,
        surcharge = surcharge
    )
}

fun MonthlyTicketTypeFirebase.mapToMonthlyTicket(): MonthlyTicketType {
    return MonthlyTicketType(
        id = id ?: "",
        numberOfMonth = numberOfMonth ?: 0.0,
        originalPrice = originalPrice ?: 0.0,
        promotionalPrice = promotionalPrice ?: 0.0,
        description = description ?: "",
        vehicleType = vehicleType ?: ""
    )
}

fun MonthlyTicketType.mapToMonthlyTicketFirebase(): MonthlyTicketTypeFirebase {
    return MonthlyTicketTypeFirebase(
        id = id,
        numberOfMonth = numberOfMonth,
        originalPrice = originalPrice,
        promotionalPrice = promotionalPrice,
        description = description,
        vehicleType = vehicleType
    )
}
