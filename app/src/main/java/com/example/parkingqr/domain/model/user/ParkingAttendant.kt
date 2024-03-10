package com.example.parkingqr.domain.model.user

class ParkingAttendant : Person() {
    var parkingLotId: String = ""
    var parkingLotManagerId: String = ""

    override fun getRole(): AccountRole {
        return AccountRole.PARKING_ATTENDANT
    }
}