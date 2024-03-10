package com.example.parkingqr.data.remote.dto.parkinglot

data class BillingTypeFirebase(
    val id: String? = null,
    val firstBlockPrice: Double? = null,
    val afterFirstBlockPrice: Double? = null,
    val firstBlock: Double? = null,
    val roundedMinutesToOneHour: Double? = null,
    val nightSurcharge: Double? = null,
    val nightTime: String? = null,
    val daylightTime: String? = null,
)