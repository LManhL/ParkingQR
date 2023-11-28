package com.example.parkingqr.data.remote.model.parking

data class VehicleResponse (
    val id: String? = null,
    val userId: String? = null,
    val licensePlate: String? = null,
    val state: String? = null,
    val brand: String? = null,
    val type: String? = null,
    val color: String? = null,
    val registrationDate: String? = null,
    val expireDate: String? = null,
    val chassisNumber: String? = null,
    val engineNumber: String? = null,
    val grossWeight: String? = null,
    val totalWeight: String? = null,
    val ownerFullName: String? = null,
    val address: String? = null,
    val seats: Double? = null,
    val certificateNumber: String? = null
)