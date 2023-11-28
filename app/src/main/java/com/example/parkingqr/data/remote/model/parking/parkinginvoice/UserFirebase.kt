package com.example.parkingqr.data.remote.model.parking.parkinginvoice

import com.example.parkingqr.domain.parking.User

class UserFirebase(
    var id: String? = "",
    var userId: String? = "",
    var name: String? = "",
    var phoneNumber: String? = ""
) {
    constructor(user: User) : this() {
        this.id = user.id
        this.userId = user.userId
        this.name = user.name
        this.phoneNumber = user.phoneNumber
    }
}