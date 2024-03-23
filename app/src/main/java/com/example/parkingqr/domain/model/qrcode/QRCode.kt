package com.example.parkingqr.domain.model.qrcode

import android.util.Log
import org.json.JSONObject

abstract class QRCode {
    companion object {
        const val LABEL_QR_CODE_TYPE = "type"
        const val USER_QR_CODE_TYPE = "userQrCodeType"
        const val INVOICE_QR_CODE_TYPE = "invoiceQrCodeType"

        fun fromString(json: String): QRCode? {
            try {
                val jsonObject = JSONObject(json)
                val type = jsonObject.getString(LABEL_QR_CODE_TYPE)
                return if (type == USER_QR_CODE_TYPE) {
                    UserQRCode.fromString(json)
                } else {
                    InvoiceQRCode.fromString(json)
                }
            } catch (e: Exception) {
                Log.e("BUG", e.toString())
            }
            return null
        }

    }

    var type: String = ""
    abstract fun getQRCodeType(): String
}