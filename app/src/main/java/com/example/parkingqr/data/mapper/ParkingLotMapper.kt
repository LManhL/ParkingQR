package com.example.parkingqr.data.mapper

import com.example.parkingqr.data.remote.dto.parkinglot.ParkingLotFirebase
import com.example.parkingqr.data.remote.dto.parkinglot.RateFirebase
import com.example.parkingqr.data.remote.dto.parkinglot.UserRateFirebase
import com.example.parkingqr.domain.model.parkinglot.ParkingLot
import com.example.parkingqr.domain.model.parkinglot.Rate
import com.example.parkingqr.domain.model.parkinglot.UserRate
import com.google.android.gms.maps.model.LatLng

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
