package com.example.parkingqr.domain.model.user

import com.example.parkingqr.data.remote.dto.user.UserResponse

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

    constructor(userResponse: UserResponse): this(){
        this.id = userResponse.id
        this.role = userResponse.role
        this.userId = userResponse.userId
        this.personalCode = userResponse.personalCode
        this.name = userResponse.name
        this.phoneNumber = userResponse.phoneNumber
        this.address = userResponse.address
        this.birthday = userResponse.birthday
        this.email = userResponse.email
        this.username = userResponse.username
        this.status = userResponse.status
    }

    fun getStatus(): UserStatus{
        return if(status == "active"){
            UserStatus.ACTIVE
        } else{
            UserStatus.BLOCKED
        }
    }

    enum class UserStatus{
        ACTIVE, BLOCKED
    }
}