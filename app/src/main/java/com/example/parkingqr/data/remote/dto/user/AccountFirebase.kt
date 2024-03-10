package com.example.parkingqr.data.remote.dto.user

class AccountFirebase() {
    var id: String? = null
    var username: String? = null
    var name: String? = null
    var address: String? = null
    var birthday: String? = null
    var email: String? = null
    var personalCode: String? = null
    var phoneNumber: String? = null
    var role: String? = null
    var status: String? = null

    constructor(
        id: String,
        username: String,
        name: String,
        address: String,
        birthday: String,
        email: String,
        personalCode: String,
        phoneNumber: String,
        role: String,
        status: String
    ): this() {
        this.id = id
        this.username = username
        this.name = name
        this.address = address
        this.birthday = birthday
        this.email = email
        this.personalCode = personalCode
        this.phoneNumber = phoneNumber
        this.role = role
        this.status = status
    }
}