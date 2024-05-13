package com.example.parkingqr.data.remote.dto.parkinglot

data class MonthlyTicketTypeFirebase(
    var id: String? = null,
    val numberOfMonth: Double? = null,
    val originalPrice: Double? = null,
    val promotionalPrice: Double? = null,
    val description: String? = null,
    val vehicleType: String? = null
)