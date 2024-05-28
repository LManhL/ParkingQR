package com.example.parkingqr.domain.model.parkinglot

data class SecurityCamera(
    var id: String = "",
    var uri: String = "",
    var type: TYPE = TYPE.TYPE_CAME_NORMAL
) {
    enum class TYPE {
        TYPE_CAM_IN, TYPE_CAM_OUT, TYPE_CAME_NORMAL
    }
}