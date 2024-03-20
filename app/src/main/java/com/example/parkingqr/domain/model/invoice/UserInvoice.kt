package com.example.parkingqr.domain.model.invoice

import com.example.parkingqr.data.remote.dto.invoice.UserInvoiceFirebase

class UserInvoice() {
    var id: String = ""
    var userId: String = ""
    var name: String = ""
    var phoneNumber: String = ""

    constructor(userInvoiceFirebase: UserInvoiceFirebase) : this() {
        this.id = userInvoiceFirebase.id?:""
        this.userId = userInvoiceFirebase.userId?:""
        this.name = userInvoiceFirebase.name?:""
        this.phoneNumber = userInvoiceFirebase.phoneNumber?:""
    }


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