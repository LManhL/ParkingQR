package com.example.parkingqr.ui.components.admin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.example.parkingqr.R
import com.example.parkingqr.domain.model.parkinglot.ParkingLot

class ParkingLotAdapter(
    private val list: MutableList<ParkingLot>,
    private val onClickItem: ((ParkingLot) -> Unit)
) :
    Adapter<ParkingLotAdapter.ParkingLotViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParkingLotViewHolder {
        return LayoutInflater.from(parent.context)
            .inflate(R.layout.item_parking_lot, parent, false).let {
                ParkingLotViewHolder(it)
            }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ParkingLotViewHolder, position: Int) {
        holder.bind(list[position])
    }

    fun addAll(data: List<ParkingLot>) {
        list.clear()
        list.addAll(data)
        notifyDataSetChanged()
    }


    inner class ParkingLotViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val name = itemView.findViewById<TextView>(R.id.tvNameParkingLot)
        private val address = itemView.findViewById<TextView>(R.id.tvAddressParkingLot)
        private val status = itemView.findViewById<TextView>(R.id.tvStatusParkingLot)

        fun bind(parkingLot: ParkingLot) {
            name.text = parkingLot.name
            address.text = parkingLot.address
            when (parkingLot.status) {
                ParkingLot.ParkingLotStatus.PENDING_STATUS -> {
                    status.text = "Chờ phê duyệt"
                    status.setTextColor(itemView.resources.getColor(R.color.light_blue))
                }
                ParkingLot.ParkingLotStatus.DECLINED_STATUS -> {
                    status.text = "Từ chối phê duyệt"
                    status.setTextColor(itemView.resources.getColor(R.color.light_red))
                }
                ParkingLot.ParkingLotStatus.ACCEPTED_STATUS -> {
                    status.text = "Đã phê duyệt"
                    status.setTextColor(itemView.resources.getColor(R.color.light_green))
                }
            }
            initListener(parkingLot)
        }

        private fun initListener(parkingLot: ParkingLot) {
            itemView.setOnClickListener {
                onClickItem.invoke(parkingLot)
            }
        }
    }
}