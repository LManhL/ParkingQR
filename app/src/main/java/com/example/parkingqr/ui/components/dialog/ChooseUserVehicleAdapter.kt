package com.example.parkingqr.ui.components.dialog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.parkingqr.R
import com.example.parkingqr.domain.model.vehicle.VehicleInvoice

class ChooseUserVehicleAdapter(
    private val list: MutableList<VehicleInvoice>,
    private val onClick: ((VehicleInvoice) -> Unit)
) : Adapter<ChooseUserVehicleAdapter.ChooseUserVehicleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChooseUserVehicleViewHolder {
        return LayoutInflater.from(parent.context)
            .inflate(R.layout.item_choose_user_vehicle, parent, false).let {
                ChooseUserVehicleViewHolder(it)
            }
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ChooseUserVehicleViewHolder, position: Int) {
        holder.bind(list[position])
    }

    inner class ChooseUserVehicleViewHolder(val itemView: View) : ViewHolder(itemView) {

        private val licensePlate =
            itemView.findViewById<TextView>(R.id.tvLicensePlateChooseUserVehicle)
        private val type = itemView.findViewById<TextView>(R.id.tvBrandChooseUserVehicle)
        private lateinit var curVehicle: VehicleInvoice

        init {
            itemView.setOnClickListener {
                onClick.invoke(curVehicle)
            }
        }

        fun bind(vehicle: VehicleInvoice) {
            curVehicle = vehicle
            licensePlate.text = vehicle.licensePlate
            type.text = "${vehicle.getVehicleType()} - ${vehicle.brand.uppercase()}"
        }
    }

}