package com.example.parkingqr.data.remote.dto.parkinglot

data class SecurityCameraFirebase(
    var id: String? = null,
    val uri: String? = null,
    val type: String? = null
) {
    companion object {
        const val TYPE_CAM_IN = "in"
        const val TYPE_CAM_OUT = "out"
        const val TYPE_CAM_NORMAL = "normal"
    }
}