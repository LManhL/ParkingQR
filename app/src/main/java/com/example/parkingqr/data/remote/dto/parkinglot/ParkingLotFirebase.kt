package com.example.parkingqr.data.remote.dto.parkinglot

import com.google.firebase.firestore.GeoPoint

data class ParkingLotFirebase(
    var id: String? = null,
    val name: String? = null,
    val location: GeoPoint? = null,
    val phoneNumber: String? = null,
    val description: String? = null,
    val address: String? = null,
    val area: Double? = null,
    val carCapacity: Double? = null,
    val motorCapacity: Double? = null,
    val images: MutableList<String> = mutableListOf(),
    val status: String? = null
) {
    companion object {
        const val ACCEPTED_STATUS = "accepted"
        const val DECLINED_STATUS = "declined"
        const val PENDING_STATUS = "pending"
    }
}
