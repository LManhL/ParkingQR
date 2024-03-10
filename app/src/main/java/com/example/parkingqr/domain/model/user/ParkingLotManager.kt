package com.example.parkingqr.domain.model.user

class ParkingLotManager : Person() {
    var parkingLotId: String = ""
    var parkingLotManagerId: String = ""

    override fun getRole(): AccountRole {
        return AccountRole.PARKING_LOT_MANAGER
    }
}