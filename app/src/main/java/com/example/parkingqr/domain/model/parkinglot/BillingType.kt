package com.example.parkingqr.domain.model.parkinglot

class BillingType(
    var id: String = "",
    var firstBlockPrice: Double = 0.0,
    var afterFirstBlockPrice: Double = 0.0,
    var firstBlock: Double = 0.0,
    var roundedMinutesToOneHour: Double = 0.0,
    var nightSurcharge: Double = 0.0,
    var nightTime: String = "",
    var daylightTime: String = "",
)