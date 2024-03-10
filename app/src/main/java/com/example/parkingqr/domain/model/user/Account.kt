package com.example.parkingqr.domain.model.user

class Account() {

    companion object {
        const val PARKING_LOT_MANAGER_ROLE = "parkingLotManager"
        const val PARKING_ATTENDANT_ROLE = "parkingAttendant"
        const val USER_ROLE = "user"
        const val ADMIN_ROLE = "admin"
        const val ACTIVE = "active"
        const val BLOCK = "blocked"
    }

    var id: String = ""
    var username: String = ""
    var name: String = ""
    var address: String = ""
    var birthday: String = ""
    var email: String = ""
    var personalCode: String = ""
    var phoneNumber: String = ""
    var role: String = ""
    var status: String = ""

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
    ) : this() {
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

    fun getAccountRole(): AccountRole {
        return when (role) {
            PARKING_LOT_MANAGER_ROLE -> AccountRole.PARKING_LOT_MANAGER
            PARKING_ATTENDANT_ROLE -> AccountRole.PARKING_ATTENDANT
            USER_ROLE -> AccountRole.USER
            else -> AccountRole.ADMIN
        }
    }

    fun setActive(){
        this.status = ACTIVE
    }

    fun setUserRole() {
        role = USER_ROLE
    }
}