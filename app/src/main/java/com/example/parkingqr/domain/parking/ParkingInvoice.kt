package com.example.parkingqr.domain.parking

import com.example.parkingqr.data.remote.model.parking.parkinginvoice.ParkingInvoiceFirebase


class ParkingInvoice() {
    var id: String = ""
    var user: User = User()
    var vehicle: Vehicle = Vehicle()
    var state: String = ""
    var imageIn: String = ""
    var imageOut: String = ""
    var timeOut: String = ""
    var price: Double = 0.0
    var timeIn: String = ""
    var paymentMethod: String = ""
    var type: String = ""
    var note: String = ""

    constructor(ID: String, user: User, vehicle: Vehicle, imageIn: String, timeIn: String): this(){
        this.id = ID
        this.user = user
        this.vehicle = vehicle
        this.state = "parking"
        this.imageIn = imageIn
        this.price = 0.0
        this.timeIn = timeIn
        this.paymentMethod = "Tiền mặt"
        this.type = "Theo giờ"
    }
    constructor(parkingInvoiceFirebase: ParkingInvoiceFirebase): this(){
        this.id = parkingInvoiceFirebase.id.toString()
        this.user = parkingInvoiceFirebase.user?.let { User(it) }!!
        this.vehicle = parkingInvoiceFirebase.vehicle?.let { Vehicle(it) }!!
        this.state = parkingInvoiceFirebase.state.toString()
        this.imageIn = parkingInvoiceFirebase.imageIn.toString()
        this.price = parkingInvoiceFirebase.price ?: 0.0
        this.timeIn = parkingInvoiceFirebase.timeIn.toString()
        this.paymentMethod = parkingInvoiceFirebase.paymentMethod.toString()
        this.type = parkingInvoiceFirebase.type.toString()
    }
}