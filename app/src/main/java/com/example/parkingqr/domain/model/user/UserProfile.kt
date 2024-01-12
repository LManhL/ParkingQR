package com.example.parkingqr.domain.model.user

import com.example.parkingqr.data.remote.dto.user.UserResponse


class UserProfile() {
    var id: String? = ""
    var userId: String? = ""
    var name: String? = ""
    var phoneNumber: String? = ""

    constructor(userResponse: UserResponse): this(){
        this.id = userResponse.id
        this.userId = userResponse.userId
        this.name = userResponse.name
        this.phoneNumber = userResponse.phoneNumber
    }
}