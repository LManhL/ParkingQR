package com.example.parkingqr.domain.model.user


class UserProfile() {
    var id: String? = ""
    var userId: String? = ""
    var name: String? = ""
    var phoneNumber: String? = ""

    constructor(
        id: String,
        userId: String,
        name: String,
        phoneNumber: String
    ) : this() {
        this.id = id
        this.userId = userId
        this.name = name
        this.phoneNumber = phoneNumber
    }

}