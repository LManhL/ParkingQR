package com.example.parkingqr.data.remote.dto.parkinglot

data class BillingTypeFirebase(
    val id: String? = null,
    val firstBlockPrice: Double? = null,
    val afterFirstBlockPrice: Double? = null,
    val firstBlock: Double? = null,
    val roundedMinutesToOneHour: Double? = null,
    val nightSurcharge: Double? = null,
    val startDaylightTime: String? = null,
    val endDaylightTime: String? = null,
    val startNightTime: String? = null,
    val endNightTime: String? = null,
    val type: String? = null,
    val vehicleType: String? = null,
    val surcharge: Double? = null
)