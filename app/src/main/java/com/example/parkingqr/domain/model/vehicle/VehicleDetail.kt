package com.example.parkingqr.domain.model.vehicle

import com.example.parkingqr.data.remote.dto.vehicle.VehicleResponse

class VehicleDetail() {
    var id: String? = null
    var createAt: String? = null
    var userId: String? = null
    var licensePlate: String? = null
    var state: String? = null
    var brand: String? = null
    var type: String? = null
    var color: String? = null
    var registrationDate: String? = null
    var expireDate: String? = null
    var chassisNumber: String? = null
    var engineNumber: String? = null
    var ownerFullName: String? = null
    var address: String? = null
    var certificateNumber: String? = null
    var images: MutableList<String> = mutableListOf()

    constructor(
        id: String?,
        createAt: String?,
        userId: String?,
        licensePlate: String?,
        state: String?,
        brand: String?,
        type: String?,
        color: String?,
        registrationDate: String?,
        expireDate: String?,
        chassisNumber: String?,
        engineNumber: String?,
        ownerFullName: String?,
        address: String?,
        certificateNumber: String?,
        images: MutableList<String>
    ) : this() {
        this.id = id
        this.createAt = createAt
        this.userId = userId
        this.licensePlate = licensePlate
        this.state = state
        this.brand = brand
        this.type = type
        this.color = color
        this.registrationDate = registrationDate
        this.expireDate = expireDate
        this.chassisNumber = chassisNumber
        this.engineNumber = engineNumber
        this.ownerFullName = ownerFullName
        this.address = address
        this.certificateNumber = certificateNumber
        this.images.addAll(images)
    }

    constructor(
        vehicleResponse: VehicleResponse
    ) : this() {
        this.id = vehicleResponse.id
        this.createAt = vehicleResponse.createAt
        this.userId = vehicleResponse.userId
        this.licensePlate = vehicleResponse.licensePlate
        this.state = vehicleResponse.state
        this.brand = vehicleResponse.brand
        this.type = vehicleResponse.type
        this.color = vehicleResponse.color
        this.registrationDate = vehicleResponse.registrationDate
        this.expireDate = vehicleResponse.expireDate
        this.chassisNumber = vehicleResponse.chassisNumber
        this.engineNumber = vehicleResponse.engineNumber
        this.ownerFullName = vehicleResponse.ownerFullName
        this.address = vehicleResponse.address
        this.certificateNumber = vehicleResponse.certificateNumber
        vehicleResponse.images?.let { this.images.addAll(it) }
    }

    fun getState(): VehicleState{
        return if(state == "unverified"){
            VehicleState.PENDING
        } else if(state == "verified"){
            VehicleState.VERIFIED
        } else{
            VehicleState.REFUSED
        }
    }

    enum class VehicleState{
        PENDING, VERIFIED, REFUSED
    }
}