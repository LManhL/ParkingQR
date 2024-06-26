package com.example.parkingqr.domain.model.qrcode

import com.example.parkingqr.utils.TimeUtil
import com.google.gson.JsonObject
import org.json.JSONObject

class UserQRCode() : QRCode() {

    companion object {
        const val USER_QR_CODE_TYPE = "userQrCodeType"
        const val LABEL_USER_ID = "userId"
        const val LABEL_TIME_IN = "timeIn"

        fun fromString(json: String): QRCode {
            val jsonObject = JSONObject(json)
            return UserQRCode(
                userId = jsonObject.getString(LABEL_USER_ID), timeIn = jsonObject.getString(
                    LABEL_TIME_IN
                )
            )
        }

    }

    var userId: String? = ""
    var timeIn: String? = ""

    constructor(userId: String, timeIn: String) : this() {
        this.userId = userId
        this.timeIn = timeIn
    }

    override fun getQRCodeType(): String {
        return USER_QR_CODE_TYPE
    }

    override fun toString(): String {
        val jsonObject = JsonObject()
        jsonObject.addProperty(LABEL_QR_CODE_TYPE, getQRCodeType())
        jsonObject.addProperty(LABEL_USER_ID, userId)
        jsonObject.addProperty(LABEL_TIME_IN, TimeUtil.getCurrentTime().toString())
        return jsonObject.toString()
    }
}