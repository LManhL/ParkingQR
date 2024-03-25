package com.example.parkingqr.data.local.user

interface UserLocalData {
    fun saveParkingLotId(parkingLotId: String)
    fun getParingLotId(): String?
}