package com.example.parkingqr.utils

object LicensePlateService {
    fun checkLicensePlateValid(licensePlate: String): Boolean{
        val pattern = Regex("^[A-Z0-9.-]{5,10}$")
        return pattern.matches(licensePlate)
    }
}