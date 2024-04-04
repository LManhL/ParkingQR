package com.example.parkingqr.domain.model.qrcode

import android.util.Log
import org.json.JSONObject

object QRCodeFactory {
    fun fromString(json: String): QRCode? {
        try {
            val jsonObject = JSONObject(json)
            val type = jsonObject.getString(QRCode.LABEL_QR_CODE_TYPE)
            return if (type == UserQRCode.USER_QR_CODE_TYPE) {
                UserQRCode.fromString(json)
            } else if(type == InvoiceQRCode.INVOICE_QR_CODE_TYPE) {
                InvoiceQRCode.fromString(json)
            }
            else{
                MonthlyTicketQRCode.fromString(json)
            }
        } catch (e: Exception) {
            Log.e("BUG", e.toString())
        }
        return null
    }
}