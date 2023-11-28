package com.example.parkingqr.domain.parking

import com.example.parkingqr.data.remote.model.parking.UserResponse
import com.example.parkingqr.data.remote.model.parking.parkinginvoice.UserFirebase

class User() {
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
    constructor(userFirebase: UserFirebase): this(){
        this.id = userFirebase.id
        this.userId = userFirebase.userId
        this.name = userFirebase.name
        this.phoneNumber = userFirebase.phoneNumber
    }
}