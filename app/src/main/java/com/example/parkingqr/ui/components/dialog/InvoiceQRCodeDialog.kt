package com.example.parkingqr.ui.components.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.widget.ImageView
import com.example.parkingqr.R

class InvoiceQRCodeDialog(context: Context, bitmap: Bitmap) {

    private var dialog = Dialog(context)
    private var bm = bitmap

    fun show() {
        dialog.setContentView(R.layout.dialog_user_qr_code)
        dialog.findViewById<ImageView>(R.id.ivQRDialogUserQRCode).setImageBitmap(bm)
        dialog.setCancelable(true)
        dialog.create()
        dialog.show()
    }

    fun dismiss() {
        dialog.dismiss()
    }
}