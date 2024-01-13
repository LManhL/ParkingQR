package com.example.parkingqr.ui.components.vehiclemanagement

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.parkingqr.R
import com.example.parkingqr.domain.model.vehicle.VehicleDetail
import com.example.parkingqr.utils.TimeService

class VehicleManagementAdapter(private val vehicleList: MutableList<VehicleDetail>) :
    Adapter<VehicleManagementAdapter.VehicleRegistrationListViewHolder>() {

    private var onClickItem: ((VehicleDetail) -> Unit)? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VehicleRegistrationListViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_vehicle_management, parent, false)
        return VehicleRegistrationListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return vehicleList.size
    }

    override fun onBindViewHolder(holder: VehicleRegistrationListViewHolder, position: Int) {
        holder.bind(vehicleList[position])
    }

    fun setClickEvent(onClick: ((VehicleDetail) -> Unit)){
        this.onClickItem = onClick
    }

    inner class VehicleRegistrationListViewHolder(itemView: View) : ViewHolder(itemView) {
        private val createAt: TextView = itemView.findViewById(R.id.tvCreateAtVehicleVehicleManagement)
        private val status: TextView = itemView.findViewById(R.id.tvStatusVehicleVehicleManagement)
        private val licensePlate: TextView = itemView.findViewById(R.id.tvLicensePlateVehicleVehicleManagement)
        private val brand: TextView = itemView.findViewById(R.id.tvBrandVehicleVehicleManagement)
        private var curVehicle: VehicleDetail? = null

        init {
            itemView.setOnClickListener {
                onClickItem?.invoke(curVehicle!!)
            }
        }

        fun bind(vehicleDetail: VehicleDetail) {
            curVehicle = vehicleDetail
            createAt.text = TimeService.convertMilisecondsToDate(vehicleDetail.createAt ?: "")
            if (vehicleDetail.getState() == VehicleDetail.VehicleState.VERIFIED) {
                status.text = "Đã phê duyệt"
                status.setTextColor(itemView.resources.getColor(R.color.light_green))
            } else if(vehicleDetail.getState() == VehicleDetail.VehicleState.PENDING) {
                status.text = "Đang chờ phê duyệt"
                status.setTextColor(itemView.resources.getColor(R.color.light_blue))
            }
            else{
                status.text = "Từ chối phê duyệt"
                status.setTextColor(itemView.resources.getColor(R.color.light_red))
            }
            licensePlate.text = vehicleDetail.licensePlate
            brand.text = "${vehicleDetail.brand} - ${vehicleDetail.type}"
        }
    }
}