package com.example.parkingqr.domain.model.user


class UserLogin() {
    var id: String? = ""
    var role: String? = ""
    var userId: String? = ""
    var name: String? = ""
    var phoneNumber: String? = ""
    var email: String? = ""

    constructor(
        id: String,
        role: String,
        userId: String,
        name: String,
        phoneNumber: String,
        email: String
    ) : this() {
        this.id = id
        this.userId = userId
        this.name = name
        this.phoneNumber = phoneNumber
        this.role = role
        this.email = email
    }
}