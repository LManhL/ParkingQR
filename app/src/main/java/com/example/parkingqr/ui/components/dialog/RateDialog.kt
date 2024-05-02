package com.example.parkingqr.ui.components.dialog

import android.app.Dialog
import android.content.Context
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.parkingqr.R
import com.example.parkingqr.domain.model.invoice.WaitingRate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RateDialog(
    private val context: Context,
    private val waitingRate: WaitingRate,
    private val sendCallBack: ((rate: Int, comment: String) -> Unit)
) {
    private lateinit var cancel: ImageView
    private lateinit var comment: EditText
    private lateinit var send: Button
    private lateinit var licensePlate: TextView
    private lateinit var vehicleType: TextView
    private lateinit var star1: ImageView
    private lateinit var star2: ImageView
    private lateinit var star3: ImageView
    private lateinit var star4: ImageView
    private lateinit var star5: ImageView
    private lateinit var stars: LinearLayout
    private var curRate: Int = 5
    private val dialog = Dialog(context)

    fun show() {
        dialog.setContentView(R.layout.dialog_rate)
        initView()
        initListener()
        val displayMetrics = context.resources.displayMetrics
        val width = (displayMetrics.widthPixels * 0.95).toInt()
        val height = (displayMetrics.heightPixels * 0.6).toInt()
        dialog.window?.setLayout(width, height)
        dialog.window?.setBackgroundDrawableResource(R.drawable.bg_user_qr_code_dialog)
        dialog.setCancelable(false)
        dialog.create()
        dialog.show()
    }

    private fun initView(){
        cancel = dialog.findViewById(R.id.ivCancelDialogRate)
        comment = dialog.findViewById(R.id.edtCommentDialogRate)
        licensePlate = dialog.findViewById(R.id.tvLicensePlateDialogRate)
        vehicleType = dialog.findViewById(R.id.tvVehicleTypeDialogRate)
        send = dialog.findViewById(R.id.btnSendDialogRate)
        star1 = dialog.findViewById(R.id.ivStar1DialogRate)
        star2 = dialog.findViewById(R.id.ivStar2DialogRate)
        star3 = dialog.findViewById(R.id.ivStar3DialogRate)
        star4 = dialog.findViewById(R.id.ivStar4DialogRate)
        star5 = dialog.findViewById(R.id.ivStar5DialogRate)
        stars = dialog.findViewById(R.id.llStarsDialogRate)
        licensePlate.text = waitingRate.licensePlate
        vehicleType.text = "${waitingRate.getReadableVehicleType()} - ${waitingRate.brand.uppercase()}"
    }

    private fun initListener() {
        cancel.setOnClickListener {
            dismiss()
        }
        star1.setOnClickListener {
            curRate = 1
            bindStars(curRate)
        }
        star2.setOnClickListener {
            curRate = 2
            bindStars(curRate)
        }
        star3.setOnClickListener {
            curRate = 3
            bindStars(curRate)
        }
        star4.setOnClickListener {
            curRate = 4
            bindStars(curRate)
        }
        star5.setOnClickListener {
            curRate = 5
            bindStars(curRate)
        }
        send.setOnClickListener {
            handleSend()
        }
    }

    private fun handleSend() {
        sendCallBack.invoke(curRate, comment.text.toString())
        dismiss()
    }

    private fun bindStars(curRate: Int) {
        for (i in 0 until 5) {
            val starImageView = stars.getChildAt(i) as ImageView
            if (i < curRate) {
                starImageView.setImageResource(R.drawable.star_rate_full)
            } else {
                starImageView.setImageResource(R.drawable.star_rate_outline)
            }
        }
    }

    private fun dismiss() {
        dialog.dismiss()
    }
}