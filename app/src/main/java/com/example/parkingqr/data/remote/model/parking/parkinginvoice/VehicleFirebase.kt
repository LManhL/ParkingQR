package com.example.parkingqr.data.remote.model.parking.parkinginvoice

import com.example.parkingqr.domain.parking.Vehicle

class VehicleFirebase(
    var id: String? = "",
    var userId: String? = "",
    var licensePlate: String? = "",
    var state: String? = "",
    var brand: String? = "",
    var type: String? = "",
    var color: String? = "",
    var ownerFullName: String? = "",
    var seats: Double? = 4.0
){
    constructor(vehicle: Vehicle): this(){
        this.id  = vehicle.id
        this.userId = vehicle.userId
        this.licensePlate = vehicle.licensePlate
        this.state = vehicle.state
        this.brand = vehicle.brand
        this.type = vehicle.type
        this.color = vehicle.color
        this.ownerFullName = vehicle.ownerFullName
        this.seats = vehicle.seats
    }
}
