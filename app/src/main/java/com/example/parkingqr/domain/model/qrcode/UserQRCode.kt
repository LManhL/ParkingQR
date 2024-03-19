package com.example.parkingqr.domain.model.qrcode

import com.example.parkingqr.utils.TimeUtil
import com.google.gson.JsonObject

class UserQRCode() {

    companion object {
        const val LABEL_USER_ID = "userId"
        const val LABEL_TIME_IN = "timeIn"
    }

    var userId: String? = ""
    var timeIn: String? = ""

    constructor(userId: String, timeIn: String) : this() {
        this.userId = userId
        this.timeIn = timeIn
    }

    override fun toString(): String {
        val jsonObject = JsonObject()
        jsonObject.addProperty(LABEL_USER_ID, userId)
        jsonObject.addProperty(LABEL_TIME_IN, TimeUtil.getCurrentTime().toString())
        return jsonObject.toString()
    }
}