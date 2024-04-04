package com.example.parkingqr.ui.components.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.parkingqr.R
import com.example.parkingqr.domain.model.parkinglot.MonthlyTicket
import com.example.parkingqr.ui.components.qrcode.Timer
import com.example.parkingqr.utils.TimeUtil

class MonthlyTicketQRCodeDialog(
    private val context: Context,
    private val bm: Bitmap,
    private val monthlyTicket: MonthlyTicket
) {
    private var dialog = Dialog(context)
    private lateinit var qrcode: ImageView
    private lateinit var timeIn: TextView
    private lateinit var timeLeft: TextView
    private lateinit var vehicleInfo: TextView
    private lateinit var parkingLotName: TextView
    private lateinit var address: TextView
    private val timeDelay: Long = Timer.SECOND_MILLISECONDS
    private val limit: Int = 60

    fun show() {
        dialog.setContentView(R.layout.dialog_monthly_ticket_qr_code)
        qrcode = dialog.findViewById(R.id.ivQRDialogMonthlyTicketQRCode)
        timeIn = dialog.findViewById(R.id.tvTimeInDialogMonthlyTicketQRCode)
        timeLeft = dialog.findViewById(R.id.tvTimeLeftDialogMonthlyTicketQRCode)
        vehicleInfo = dialog.findViewById(R.id.tvVehicleInfoDialogMonthlyTicketQRCode)
        parkingLotName = dialog.findViewById(R.id.tvParkingLotNameDialogMonthlyTicketQRCode)
        address = dialog.findViewById(R.id.tvAddressDialogMonthlyTicketQRCode)

        val displayMetrics = context.resources.displayMetrics
        val width = (displayMetrics.widthPixels * 0.95).toInt()
        val height = (displayMetrics.heightPixels * 0.8).toInt()

        dialog.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawableResource(R.drawable.bg_user_qr_code_dialog)

        initView()

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

    private fun initView() {
        timeIn.text = "${TimeUtil.getDateCurrentTime()}"
        qrcode.setImageBitmap(bm)
        vehicleInfo.text =
            "${monthlyTicket.vehicle.licensePlate} - ${monthlyTicket.vehicle.getVehicleType()} - ${monthlyTicket.vehicle.brand?.uppercase()}"
        parkingLotName.text = monthlyTicket.parkingLot.name
        address.text = monthlyTicket.parkingLot.address
    }

    fun dismiss() {
        dialog.dismiss()
    }
}