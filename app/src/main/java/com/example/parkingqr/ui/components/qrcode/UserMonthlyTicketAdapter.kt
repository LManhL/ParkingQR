package com.example.parkingqr.ui.components.qrcode

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.parkingqr.R
import com.example.parkingqr.domain.model.parkinglot.MonthlyTicket
import com.example.parkingqr.utils.TimeUtil

class UserMonthlyTicketAdapter(private val list: MutableList<MonthlyTicket>) :
    Adapter<UserMonthlyTicketAdapter.UserMonthlyTicketViewHolder>() {

    private var curSelect: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserMonthlyTicketViewHolder {
        return LayoutInflater.from(parent.context)
            .inflate(R.layout.item_monthly_ticket, parent, false).let {
                UserMonthlyTicketViewHolder(it)
            }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: UserMonthlyTicketViewHolder, position: Int) {
        list[position].let {
            holder.bind(it, position)
        }
    }

    fun selectItem(newPos: Int) {
        val oldPos = curSelect
        curSelect = newPos
        notifyItemChanged(oldPos)
        notifyItemChanged(newPos)
    }

    inner class UserMonthlyTicketViewHolder(val itemView: View) : ViewHolder(itemView) {
        private val licensePlate =
            itemView.findViewById<TextView>(R.id.tvLicensePlateUserMonthlyTicket)
        private val status = itemView.findViewById<TextView>(R.id.tvExpireDateUserMonthlyTicket)
        private val select = itemView.findViewById<ImageView>(R.id.ivSelectMonthlyTicket)
        private val parkingLotName =
            itemView.findViewById<TextView>(R.id.tvParkingLotNameUserMonthlyTicket)
        private val address = itemView.findViewById<TextView>(R.id.tvAddressUserMonthlyTicket)
        private var curItem: MonthlyTicket? = null
        private var curPos: Int? = null

        init {
            itemView.setOnClickListener {
                curPos?.let {
                    selectItem(it)
                }
            }
        }

        fun bind(monthlyTicket: MonthlyTicket, position: Int) {
            curItem = monthlyTicket
            curPos = position
            curItem?.apply {
                licensePlate.text = "${vehicle.licensePlate?.uppercase()} - ${vehicle.getVehicleType()} - ${vehicle.brand?.uppercase()}"
                select.visibility = View.GONE
                curSelect.takeIf { curSelect == curPos }?.let {
                    select.visibility = View.VISIBLE
                }
                parkingLotName.text = parkingLot.name
                address.text = parkingLot.address
                if (expiredAt.toLong() > TimeUtil.getCurrentTime()) {
                    status.text = "Còn hạn sử dụng"
                    status.setTextColor(itemView.resources.getColor(R.color.light_green))
                } else {
                    status.text = "Hết hạn sử dụng"
                    status.setTextColor(itemView.resources.getColor(R.color.light_red))
                }
            }
        }
    }
}