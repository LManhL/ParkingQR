package com.example.parkingqr.domain.model.user

class UserDetail() {
    var id: String? = ""
    var role: String? = ""
    var status: String? = ""
    var userId: String? = ""
    var personalCode: String? = ""
    var name: String? = ""
    var phoneNumber: String? = ""
    var address: String? = ""
    var birthday: String? = ""
    var email: String? = ""
    var username: String? = ""

    constructor(
        id: String,
        role: String,
        status: String,
        userId: String,
        personalCode: String,
        name: String,
        phoneNumber: String,
        address: String,
        birthday: String,
        email: String,
        username: String
    ) : this() {
        this.id = id
        this.role = role
        this.userId = userId
        this.personalCode = personalCode
        this.name = name
        this.phoneNumber = phoneNumber
        this.address = address
        this.birthday = birthday
        this.email = email
        this.username = username
        this.status = status
    }

    fun getStatus(): UserStatus {
        return if (status == "active") {
            UserStatus.ACTIVE
        } else {
            UserStatus.BLOCKED
        }
    }

    enum class UserStatus {
        ACTIVE, BLOCKED
    }
}