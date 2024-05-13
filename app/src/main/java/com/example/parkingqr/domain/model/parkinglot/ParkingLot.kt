package com.example.parkingqr.domain.model.parkinglot

import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint

class ParkingLot(
    var id: String = "",
    var name: String = "",
    var location: LatLng = LatLng(0.0, 0.0),
    var phoneNumber: String = "",
    var description: String = "",
    var address: String = "",
    var area: Double = 0.0,
    val carCapacity: Double = 0.0,
    val motorCapacity: Double = 0.0,
    val images: MutableList<String> = mutableListOf(),
    val status: ParkingLotStatus = ParkingLotStatus.PENDING_STATUS
) {
    enum class ParkingLotStatus {
        ACCEPTED_STATUS,
        DECLINED_STATUS,
        PENDING_STATUS
    }
}