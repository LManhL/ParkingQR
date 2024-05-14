package com.example.parkingqr.ui.components.location

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.example.parkingqr.R
import com.example.parkingqr.domain.model.parkinglot.ParkingLot
import com.example.parkingqr.domain.model.parkinglot.ParkingLotWithDistance
import com.example.parkingqr.utils.FormatCurrencyUtil

class RecentDisParkingLotAdapter(
    private val list: MutableList<ParkingLotWithDistance>,
    private val onClickItem: ((ParkingLotWithDistance) -> Unit)
) :
    Adapter<RecentDisParkingLotAdapter.ParkingLotViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParkingLotViewHolder {
        return LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recent_distance_list, parent, false).let {
                ParkingLotViewHolder(it)
            }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ParkingLotViewHolder, position: Int) {
        holder.bind(list[position])
    }

    fun addAll(data: List<ParkingLotWithDistance>) {
        list.clear()
        list.addAll(data)
        notifyDataSetChanged()
    }


    inner class ParkingLotViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val name = itemView.findViewById<TextView>(R.id.tvNameRecentDistanceList)
        private val address = itemView.findViewById<TextView>(R.id.tvAddressRecentDistanceList)
        private val distance = itemView.findViewById<TextView>(R.id.tvDistanceRecentDistanceList)

        fun bind(data: ParkingLotWithDistance) {
            name.text = data.parkingLot.name
            address.text = data.parkingLot.address
            distance.text = "Cách bạn ${FormatCurrencyUtil.formatNumberCeil(data.distance)}km"
            initListener(data)
        }

        private fun initListener(data: ParkingLotWithDistance) {
            itemView.setOnClickListener {
                onClickItem.invoke(data)
            }
        }
    }
}