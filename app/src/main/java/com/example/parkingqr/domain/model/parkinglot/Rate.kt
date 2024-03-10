package com.example.parkingqr.domain.model.parkinglot

class Rate(
    var id: String = "",
    var parkingLotId: String = "",
    var rate: Double = 0.0,
    var comment: String = "",
    var createAt: String = "",
    var userRate: UserRate = UserRate()
)

class UserRate(
    var userId: String = "",
    var name: String = "",
    var avatar: String = ""
)