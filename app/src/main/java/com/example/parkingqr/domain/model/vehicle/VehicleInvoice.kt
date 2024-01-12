package com.example.parkingqr.domain.model.vehicle

import com.example.parkingqr.data.remote.dto.vehicle.VehicleResponse
import com.example.parkingqr.data.remote.dto.invoice.VehicleInvoiceFirebase

class VehicleInvoice() {

    var id: String? = ""
    var userId: String? = ""
    var licensePlate: String? = ""
    var state: String? = ""
    var brand: String? = ""
    var type: String? = ""
    var color: String? = ""
    var ownerFullName: String? = ""

    constructor(vehicleResponse: VehicleResponse): this(){
       this.id = vehicleResponse.id
       this.userId = vehicleResponse.userId
       this.licensePlate = vehicleResponse.licensePlate
       this.state = vehicleResponse.state
       this.brand = vehicleResponse.brand
       this.type = vehicleResponse.type
       this.color = vehicleResponse.color
       this.ownerFullName = vehicleResponse.ownerFullName
    }
    constructor(vehicleInvoiceFirebase: VehicleInvoiceFirebase): this(){
        this.id = vehicleInvoiceFirebase.id
        this.userId = vehicleInvoiceFirebase.userId
        this.licensePlate = vehicleInvoiceFirebase.licensePlate
        this.state = vehicleInvoiceFirebase.state
        this.brand = vehicleInvoiceFirebase.brand
        this.type = vehicleInvoiceFirebase.type
        this.color = vehicleInvoiceFirebase.color
        this.ownerFullName = vehicleInvoiceFirebase.ownerFullName
    }
    constructor(licensePlate: String): this(){
        this.licensePlate = licensePlate
    }
}