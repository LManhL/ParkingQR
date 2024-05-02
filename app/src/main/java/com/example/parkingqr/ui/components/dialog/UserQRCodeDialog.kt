package com.example.parkingqr.ui.components.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.parkingqr.R

class UserQRCodeDialog(
    private val context: Context,
    private val bm: Bitmap,
    private val createAt: String
) {
    private var dialog = Dialog(context)
    private lateinit var qrcode: ImageView
    private lateinit var timeIn: TextView
    private lateinit var timeLeft: TextView
    private val timeDelay: Long = Timer.SECOND_MILLISECONDS
    private val limit: Int = 60

    fun show() {
        dialog.setContentView(R.layout.dialog_user_qr_code)
        qrcode = dialog.findViewById(R.id.ivQRDialogUserQRCode)
        timeIn = dialog.findViewById(R.id.tvTimeInDialogUserQRCode)
        timeLeft = dialog.findViewById(R.id.tvTimeLeftDialogUserQRCode)

        val displayMetrics = context.resources.displayMetrics
        val width = (displayMetrics.widthPixels * 0.95).toInt()
        val height = (displayMetrics.heightPixels * 0.6).toInt()

        dialog.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawableResource(R.drawable.bg_user_qr_code_dialog)

        qrcode.setImageBitmap(bm)
        timeIn.text = createAt
        dialog.setCancelable(true)
        dialog.create()
        dialog.show()

        val timer = object : Timer(timeDelay, limit) {
            override fun onWorking(count: Int) {
                timeLeft.text = ((limit - count).toString())
            }

            override fun onFinish() {
                dismiss()
            }
        }
        timer.start()

    }

    fun dismiss() {
        dialog.dismiss()
    }
}