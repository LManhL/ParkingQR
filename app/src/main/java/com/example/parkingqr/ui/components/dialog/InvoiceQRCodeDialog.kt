package com.example.parkingqr.ui.components.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.view.ViewGroup
import android.widget.ImageView
import com.example.parkingqr.R

class InvoiceQRCodeDialog(private val context: Context, private val bm: Bitmap) {

    private var dialog = Dialog(context)

    fun show() {
        dialog.setContentView(R.layout.dialog_invoice_qr_code)
        val displayMetrics = context.resources.displayMetrics
        val width = (displayMetrics.widthPixels * 0.95).toInt()
        val height = (displayMetrics.heightPixels * 0.6).toInt()

        dialog.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawableResource(R.drawable.bg_user_qr_code_dialog)

        dialog.findViewById<ImageView>(R.id.ivQRCodeDialogInvoiceQRCode).setImageBitmap(bm)
        dialog.setCancelable(true)
        dialog.create()
        dialog.show()
    }

    fun dismiss() {
        dialog.dismiss()
    }
}