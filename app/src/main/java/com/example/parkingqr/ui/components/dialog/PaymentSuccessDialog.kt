package com.example.parkingqr.ui.components.dialog

import android.app.Dialog
import android.content.Context
import android.widget.ProgressBar
import android.widget.TextView
import com.example.parkingqr.R
import com.example.parkingqr.ui.components.qrcode.Timer

class PaymentSuccessDialog(context: Context, private val secondDelay: Long, private val onFinish: (() -> Unit)) {
    private val dialog = Dialog(context)
    private val timeDelay: Long = secondDelay * Timer.SECOND_MILLISECONDS / 1000
    private val limit = 1000
    private val numberTimeToASecond: Int = (Timer.SECOND_MILLISECONDS / timeDelay).toInt()
    private lateinit var progressBar: ProgressBar
    private lateinit var time: TextView

    fun show() {
        dialog.setContentView(R.layout.dialog_payment_success)
        progressBar = dialog.findViewById(R.id.pgPaymentSuccessDialog)
        time = dialog.findViewById(R.id.tvTimePaymentSuccessDialog)

        dialog.setCancelable(false)
        dialog.create()
        dialog.show()

        val timer = object : Timer(timeDelay, limit) {
            override fun onWorking(count: Int) {
                progressBar.progress = count
                if (count % numberTimeToASecond == 0) time.text = "Chuyển hướng sau ${secondDelay - (count / numberTimeToASecond)}s"
            }

            override fun onFinish() {
                onFinish.invoke()
                dismiss()
            }
        }
        timer.start()
    }

    fun dismiss() {
        dialog.dismiss()
    }
}