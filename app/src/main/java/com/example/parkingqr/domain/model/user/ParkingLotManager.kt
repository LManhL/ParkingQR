package com.example.parkingqr.domain.model.user

class ParkingLotManager() : Person() {
    var parkingLotId: String = ""
    var parkingLotManagerId: String = ""

    constructor(
        id: String,
        parkingLotId: String,
        parkingLotManagerId: String,
        account: Account
    ) : this() {
        this.id = id
        this.parkingLotManagerId = parkingLotManagerId
        this.parkingLotId = parkingLotId
        this.account = account
    }

    override fun getRole(): AccountRole {
        return AccountRole.PARKING_LOT_MANAGER
    }
}