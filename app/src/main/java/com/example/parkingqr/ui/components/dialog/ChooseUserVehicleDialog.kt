package com.example.parkingqr.ui.components.dialog

import android.app.Dialog
import android.content.Context
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.parkingqr.R
import com.example.parkingqr.domain.model.vehicle.VehicleInvoice

class ChooseUserVehicleDialog(
    private val context: Context,
    private val vehicleList: List<VehicleInvoice>,
    private val curLicensePlate: String,
    private val onSelect: ((VehicleInvoice) -> Unit)
) {
    private val dialog = Dialog(context)
    private lateinit var tvLicensePlate: TextView
    private lateinit var list: RecyclerView
    private lateinit var adapter: ChooseUserVehicleAdapter

    fun show() {
        dialog.setContentView(R.layout.dialog_choose_user_vehicle)
        initView()
        val displayMetrics = context.resources.displayMetrics
        val width = (displayMetrics.widthPixels * 0.9).toInt()
        val height = (displayMetrics.heightPixels * 0.35).toInt()
        dialog.window?.setLayout(width, height)
        dialog.window?.setBackgroundDrawableResource(R.drawable.bg_user_qr_code_dialog)
        dialog.setCancelable(true)
        dialog.create()
        dialog.show()
    }

    fun dismiss() {
        dialog.dismiss()
    }

    private fun initView() {
        tvLicensePlate = dialog.findViewById(R.id.tvLicensePlateDialogChooseUserVehicle)
        list = dialog.findViewById(R.id.rlvVehicleListDialogChooseUserVehicle)
        tvLicensePlate.text = curLicensePlate
        adapter = ChooseUserVehicleAdapter(vehicleList.toMutableList()) {
            handleClick(it)
        }
        list.adapter = adapter
        list.layoutManager =
            LinearLayoutManager(dialog.context, LinearLayoutManager.VERTICAL, false)
    }

    private fun handleClick(vehicle: VehicleInvoice) {
        onSelect.invoke(vehicle)
        dialog.dismiss()
    }
}