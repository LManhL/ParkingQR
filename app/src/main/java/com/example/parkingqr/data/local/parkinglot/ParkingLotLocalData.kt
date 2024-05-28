package com.example.parkingqr.data.local.parkinglot

interface ParkingLotLocalData {
    fun setUriCameraIn(uri: String)
    fun setUriCameraOut(uri: String)
    fun getUriCameraIn(): String?
    fun getUriCameraOut(): String?
}