package com.example.parkingqr.ui.components.dialog

import android.app.Dialog
import android.content.Context
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import androidx.cardview.widget.CardView
import com.example.parkingqr.R
import com.example.parkingqr.domain.model.vehicle.VehicleInvoice

class ChooseVehicleTypeDialog(
    private val context: Context,
    private val confirmCallback: ((String) -> Unit)
) {

    private var dialog = Dialog(context)
    private lateinit var motorbike: RadioButton
    private lateinit var car: RadioButton
    private lateinit var other: RadioButton
    private lateinit var confirm: Button
    private lateinit var crdMotorbike: CardView
    private lateinit var crdCar: CardView
    private lateinit var crdOther: CardView

    fun show() {
        dialog.setContentView(R.layout.dialog_choose_vehicle_type)
        val displayMetrics = context.resources.displayMetrics
        val width = (displayMetrics.widthPixels * 0.95).toInt()
        val height = (displayMetrics.heightPixels * 0.6).toInt()
        motorbike = dialog.findViewById<RadioButton>(R.id.rdMotobikeDialogChooseVehicleType)
        car = dialog.findViewById<RadioButton>(R.id.rdCarDialogChooseVehicleType)
        other = dialog.findViewById<RadioButton>(R.id.rdOtherDialogChooseVehicleType)
        crdMotorbike = dialog.findViewById<CardView>(R.id.crdMotobikeDialogChooseVehicleType)
        crdCar = dialog.findViewById<CardView>(R.id.crdCarDialogChooseVehicleType)
        crdOther = dialog.findViewById<CardView>(R.id.crdOtherDialogChooseVehicleType)
        confirm = dialog.findViewById<Button>(R.id.btnConfirmDialogChooseVehicleType)

        dialog.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawableResource(R.drawable.bg_user_qr_code_dialog)

        dialog.setCancelable(true)
        dialog.create()
        dialog.show()

        crdMotorbike.setOnClickListener {
            motorbike.isChecked = true
            car.isChecked = false
            other.isChecked = false
        }

        crdCar.setOnClickListener {
            motorbike.isChecked = false
            other.isChecked = false
            car.isChecked = true
        }

        crdOther.setOnClickListener {
            motorbike.isChecked = false
            car.isChecked = false
            other.isChecked = true
        }

        confirm.setOnClickListener {
            val type = if (motorbike.isChecked) {
                VehicleInvoice.MOTORBIKE_TYPE
            } else if (car.isChecked) {
                VehicleInvoice.CAR_TYPE
            } else {
                VehicleInvoice.OTHER_TYPE
            }
            confirmCallback.invoke(type)
            dismiss()
        }

    }

    fun dismiss() {
        dialog.dismiss()
    }

}