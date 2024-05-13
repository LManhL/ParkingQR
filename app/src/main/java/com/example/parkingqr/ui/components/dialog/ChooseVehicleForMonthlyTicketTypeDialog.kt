package com.example.parkingqr.ui.components.dialog

import android.app.Dialog
import android.content.Context
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.example.parkingqr.R
import com.example.parkingqr.domain.model.vehicle.VehicleInvoice

class ChooseVehicleForMonthlyTicketTypeDialog(
    private val context: Context,
    private val confirmCallback: ((String) -> Unit)
) {

    private var dialog = Dialog(context)
    private lateinit var motorbike: RadioButton
    private lateinit var car: RadioButton
    private lateinit var confirm: Button
    private lateinit var crdMotorbike: CardView
    private lateinit var crdCar: CardView
    private lateinit var question: TextView

    fun show() {
        dialog.setContentView(R.layout.dialog_choose_vehicle_type)
        val displayMetrics = context.resources.displayMetrics
        val width = (displayMetrics.widthPixels * 0.95).toInt()
        val height = (displayMetrics.heightPixels * 0.6).toInt()
        question = dialog.findViewById(R.id.tvQuestionDialogChooseVehicleType)
        motorbike = dialog.findViewById(R.id.rdMotobikeDialogChooseVehicleType)
        car = dialog.findViewById(R.id.rdCarDialogChooseVehicleType)
        crdMotorbike = dialog.findViewById(R.id.crdMotobikeDialogChooseVehicleType)
        crdCar = dialog.findViewById(R.id.crdCarDialogChooseVehicleType)
        confirm = dialog.findViewById(R.id.btnConfirmDialogChooseVehicleType)

        question.text = "Chọn loại xe bạn muốn tạo vé tháng"
        confirm.text = "Xác nhận"

        dialog.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawableResource(R.drawable.bg_user_qr_code_dialog)

        dialog.setCancelable(true)
        dialog.create()
        dialog.show()

        crdMotorbike.setOnClickListener {
            motorbike.isChecked = true
            car.isChecked = false
        }

        crdCar.setOnClickListener {
            motorbike.isChecked = false
            car.isChecked = true
        }

        confirm.setOnClickListener {
            val type = if (motorbike.isChecked) {
                VehicleInvoice.MOTORBIKE_TYPE
            } else {
                VehicleInvoice.CAR_TYPE
            }
            confirmCallback.invoke(type)
            dismiss()
        }

    }

    fun dismiss() {
        dialog.dismiss()
    }

}