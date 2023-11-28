package com.example.parkingqr.data.remote.model.parking.parkinginvoice

import com.example.parkingqr.domain.parking.ParkingInvoice

data class ParkingInvoiceFirebase(
    var id: String? = null,
    var user: UserFirebase? = null,
    var vehicle: VehicleFirebase? = null,
    var state: String? = null,
    var imageIn: String? = null,
    var imageOut: String? = null,
    var price: Double? = null,
    var timeIn: String? = null,
    var timeOut: String? = null,
    var paymentMethod: String? = null,
    var type: String? = null,
    var note: String? = null
    ){
    constructor(parkingInvoice: ParkingInvoice): this(){
        this.id = parkingInvoice.id
        this.imageIn = parkingInvoice.imageIn
        this.state = parkingInvoice.state
        this.imageOut = this.imageOut
        this.price = parkingInvoice.price
        this.paymentMethod = parkingInvoice.paymentMethod
        this.type = parkingInvoice.type
        this.note = parkingInvoice.note
        this.timeIn = parkingInvoice.timeIn
        this.timeOut = this.timeOut
        this.user = UserFirebase(parkingInvoice.user)
        this.vehicle = VehicleFirebase(parkingInvoice.vehicle)
    }
}