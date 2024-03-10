package com.example.parkingqr.data.remote.dto.parkinglot

import com.google.firebase.firestore.GeoPoint

data class ParkingLotFirebase(
    val id: String? = null,
    val name: String? = null,
    val location: GeoPoint? = null,
    val phoneNumber: String? = null,
    val capacity: String? = null,
    val description: String? = null,
    val address: String? = null,
    val area: Double? = null,
)
