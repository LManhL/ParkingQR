package com.example.parkingqr.domain.model.qrcode

import com.example.parkingqr.utils.TimeUtil
import com.google.gson.JsonObject
import org.json.JSONObject

class MonthlyTicketQRCode() : QRCode() {

    companion object {
        const val MONTHLY_QR_CODE_TYPE = "monthlyTicketQrCodeType"
        const val LABEL_TIME_IN = "timeIn"
        const val LABEL_MONTHLY_TICKET_ID = "monthlyTicketId"

        fun fromString(json: String): QRCode {
            val jsonObject = JSONObject(json)
            return MonthlyTicketQRCode(
                monthlyTicketId = jsonObject.getString(LABEL_MONTHLY_TICKET_ID),
                timeIn = jsonObject.getString(
                    LABEL_TIME_IN
                )
            )
        }
    }

    var monthlyTicketId: String = ""
    var timeIn: String = ""

    constructor(monthlyTicketId: String, timeIn: String) : this() {
        this.monthlyTicketId = monthlyTicketId
        this.timeIn = timeIn
    }

    override fun getQRCodeType(): String {
        return MONTHLY_QR_CODE_TYPE
    }

    override fun toString(): String {
        val jsonObject = JsonObject()
        jsonObject.addProperty(LABEL_QR_CODE_TYPE, getQRCodeType())
        jsonObject.addProperty(LABEL_MONTHLY_TICKET_ID, monthlyTicketId)
        jsonObject.addProperty(LABEL_TIME_IN, TimeUtil.getCurrentTime().toString())
        return jsonObject.toString()
    }
}