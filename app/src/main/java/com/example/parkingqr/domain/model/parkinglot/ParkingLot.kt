package com.example.parkingqr.domain.model.parkinglot

import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint

class ParkingLot(
    var id: String = "",
    var name: String = "",
    var location: LatLng = LatLng(0.0, 0.0),
    var phoneNumber: String = "",
    var capacity: String = "",
    var description: String = "",
    var address: String = "",
    var area: Double = 0.0,
    var billingTypes: MutableList<BillingType> = mutableListOf(),
    var rates: MutableList<Rate> = mutableListOf()
)