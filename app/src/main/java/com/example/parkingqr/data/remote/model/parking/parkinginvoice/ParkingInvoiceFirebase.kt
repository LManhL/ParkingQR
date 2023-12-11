package com.example.parkingqr.data.remote.model.parking.parkinginvoice

import com.example.parkingqr.domain.parking.ParkingInvoicePK

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
    constructor(parkingInvoicePK: ParkingInvoicePK): this(){
        this.id = parkingInvoicePK.id
        this.imageIn = parkingInvoicePK.imageIn
        this.state = parkingInvoicePK.state
        this.imageOut = parkingInvoicePK.imageOut
        this.price = parkingInvoicePK.price
        this.paymentMethod = parkingInvoicePK.paymentMethod
        this.type = parkingInvoicePK.type
        this.note = parkingInvoicePK.note
        this.timeIn = parkingInvoicePK.timeIn
        this.timeOut = parkingInvoicePK.timeOut
        this.user = UserFirebase(parkingInvoicePK.user)
        this.vehicle = VehicleFirebase(parkingInvoicePK.vehicle)
    }
}