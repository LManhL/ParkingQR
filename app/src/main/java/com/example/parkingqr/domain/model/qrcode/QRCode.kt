package com.example.parkingqr.domain.model.qrcode

import org.json.JSONObject

abstract class QRCode {
    companion object {
        const val LABEL_QR_CODE_TYPE = "type"
        const val USER_QR_CODE_TYPE = "userQrCodeType"
        const val INVOICE_QR_CODE_TYPE = "invoiceQrCodeType"

        fun fromString(json: String): QRCode {
            val jsonObject = JSONObject(json)
            val type = jsonObject.getString(LABEL_QR_CODE_TYPE)
            return if (type == USER_QR_CODE_TYPE) {
                UserQRCode.fromString(json)
            } else {
                InvoiceQRCode.fromString(json)
            }
        }

    }

    var type: String = ""
    abstract fun getQRCodeType(): String
}