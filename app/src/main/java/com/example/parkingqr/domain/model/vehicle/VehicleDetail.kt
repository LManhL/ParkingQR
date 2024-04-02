package com.example.parkingqr.domain.model.vehicle

class VehicleDetail() {

    companion object {
        const val CAR_TYPE = "car"
        const val MOTORBIKE_TYPE = "motorbike"
        const val OTHER_TYPE = "other"
    }

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

    fun getState(): VehicleState{
        return if(state == "unverified"){
            VehicleState.PENDING
        } else if(state == "verified"){
            VehicleState.VERIFIED
        } else{
            VehicleState.REFUSED
        }
    }
    fun getVehicleType(): String {
        return if (type == VehicleInvoice.CAR_TYPE) {
            "Xe hơi"
        } else if (type == VehicleInvoice.MOTORBIKE_TYPE) {
            "Xe máy"
        } else "Khác"
    }

    enum class VehicleState{
        PENDING, VERIFIED, REFUSED
    }
}