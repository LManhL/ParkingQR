package com.example.parkingqr.data.remote.dto.user

class ParkingAttendantFirebase : PersonFirebase() {
    var parkingLotId: String? = null
    var parkingLotManagerId: String? = null
}