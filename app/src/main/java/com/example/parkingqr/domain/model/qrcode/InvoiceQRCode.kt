package com.example.parkingqr.domain.model.qrcode

import com.example.parkingqr.utils.TimeUtil
import com.google.gson.JsonObject
import org.json.JSONObject

class InvoiceQRCode() : QRCode() {

    companion object {
        const val INVOICE_QR_CODE_TYPE = "invoiceQrCodeType"
        const val LABEL_INVOICE_ID = "invoiceId"
        const val LABEL_TIME_OUT = "timeOut"

        fun fromString(json: String): QRCode {
            val jsonObject = JSONObject(json)
            return InvoiceQRCode(
                invoiceId = jsonObject.getString(LABEL_INVOICE_ID), timeOut = jsonObject.getString(
                    LABEL_TIME_OUT
                )
            )
        }

    }

    var invoiceId: String? = ""
    var timeOut: String? = ""

    constructor(invoiceId: String, timeOut: String) : this() {
        this.invoiceId = invoiceId
        this.timeOut = timeOut
    }

    override fun getQRCodeType(): String {
        return INVOICE_QR_CODE_TYPE
    }

    override fun toString(): String {
        val jsonObject = JsonObject()
        jsonObject.addProperty(LABEL_QR_CODE_TYPE, getQRCodeType())
        jsonObject.addProperty(LABEL_INVOICE_ID, invoiceId)
        jsonObject.addProperty(LABEL_TIME_OUT, TimeUtil.getCurrentTime().toString())
        return jsonObject.toString()
    }
}