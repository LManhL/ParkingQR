package com.example.parkingqr.domain.model.qrcode

import android.util.Log
import org.json.JSONObject

abstract class QRCode {
    companion object {
        const val LABEL_QR_CODE_TYPE = "type"
    }

    var type: String = ""
    abstract fun getQRCodeType(): String
}