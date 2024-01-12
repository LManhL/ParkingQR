package com.example.parkingqr.domain.model.user

import com.example.parkingqr.data.remote.dto.user.UserResponse


class UserLogin() {
    var id: String? = ""
    var role: String? = ""
    var userId: String? = ""
    var name: String? = ""
    var phoneNumber: String? = ""
    var email: String? = ""

    constructor(userResponse: UserResponse): this(){
        this.id = userResponse.id
        this.userId = userResponse.userId
        this.name = userResponse.name
        this.phoneNumber = userResponse.phoneNumber
        this.role = userResponse.role
        this.email = userResponse.email
    }
}