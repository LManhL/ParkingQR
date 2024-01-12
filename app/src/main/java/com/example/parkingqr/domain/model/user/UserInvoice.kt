package com.example.parkingqr.domain.model.user

import com.example.parkingqr.data.remote.dto.user.UserResponse
import com.example.parkingqr.data.remote.dto.invoice.UserInvoiceFirebase

class UserInvoice() {
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
    constructor(userInvoiceFirebase: UserInvoiceFirebase): this(){
        this.id = userInvoiceFirebase.id
        this.userId = userInvoiceFirebase.userId
        this.name = userInvoiceFirebase.name
        this.phoneNumber = userInvoiceFirebase.phoneNumber
    }
}