package com.example.parkingqr.domain.parking

import com.example.parkingqr.data.remote.model.parking.VehicleResponse
import com.example.parkingqr.data.remote.model.parking.parkinginvoice.VehicleFirebase

class Vehicle() {

    var id: String? = ""
    var userId: String? = ""
    var licensePlate: String? = ""
    var state: String? = ""
    var brand: String? = ""
    var type: String? = ""
    var color: String? = ""
    var ownerFullName: String? = ""
    var seats: Double? = 0.0

    constructor(vehicleResponse: VehicleResponse): this(){
       this.id = vehicleResponse.id
       this.userId = vehicleResponse.userId
       this.licensePlate = vehicleResponse.licensePlate
       this.state = vehicleResponse.state
       this.brand = vehicleResponse.brand
       this.type = vehicleResponse.type
       this.color = vehicleResponse.color
       this.ownerFullName = vehicleResponse.ownerFullName
       this.seats = vehicleResponse.seats
    }
    constructor(vehicleFirebase: VehicleFirebase): this(){
        this.id = vehicleFirebase.id
        this.userId = vehicleFirebase.userId
        this.licensePlate = vehicleFirebase.licensePlate
        this.state = vehicleFirebase.state
        this.brand = vehicleFirebase.brand
        this.type = vehicleFirebase.type
        this.color = vehicleFirebase.color
        this.ownerFullName = vehicleFirebase.ownerFullName
        this.seats = vehicleFirebase.seats
    }
    constructor(licensePlate: String): this(){
        this.licensePlate = licensePlate
    }
}