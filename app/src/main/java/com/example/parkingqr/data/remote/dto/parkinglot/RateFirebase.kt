package com.example.parkingqr.data.remote.dto.parkinglot

data class RateFirebase(
    val id: String? = null,
    val parkingLotId: String? = null,
    val rate: Double? = null,
    val comment: String? = null,
    val createAt: String? = null,
    val user: UserRateFirebase? = null
)
data class UserRateFirebase(
    val userId: String? = null,
    val name: String? = null,
    val avatar: String? = null
)