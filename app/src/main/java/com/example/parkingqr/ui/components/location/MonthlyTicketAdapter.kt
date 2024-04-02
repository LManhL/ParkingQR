package com.example.parkingqr.ui.components.location

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.example.parkingqr.R
import com.example.parkingqr.domain.model.parkinglot.MonthlyTicketType
import com.example.parkingqr.utils.FormatCurrencyUtil

class MonthlyTicketAdapter(private val list: MutableList<MonthlyTicketType>) :
    Adapter<MonthlyTicketAdapter.MonthlyTicketViewHolder>() {

    private var choosePosition: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonthlyTicketViewHolder {
        return LayoutInflater.from(parent.context)
            .inflate(R.layout.item_register_monthly_invoice, parent, false).let {
                MonthlyTicketViewHolder(it)
            }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MonthlyTicketViewHolder, position: Int) {
        val value = list[position]
        holder.bind(value, position)
    }

    fun getSelectedMonthlyTicket() = list[choosePosition]

    fun chooseMonthlyTicket(newPosition: Int) {
        val oldPos = choosePosition
        choosePosition = newPosition
        notifyItemChanged(oldPos)
        notifyItemChanged(newPosition)
    }

    inner class MonthlyTicketViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val container =
            itemView.findViewById<CardView>(R.id.cdvContainerRegisterMonthlyInvoice)
        private val time = itemView.findViewById<TextView>(R.id.tvTimeRegisterMonthlyInvoice)
        private val description =
            itemView.findViewById<TextView>(R.id.tvDescriptionRegisterMonthlyInvoice)
        private val oldPrice =
            itemView.findViewById<TextView>(R.id.tvOldPriceRegisterMonthlyInvoice)
        private val newPrice =
            itemView.findViewById<TextView>(R.id.tvNewPriceRegisterMonthlyInvoice)
        private val choose =
            itemView.findViewById<RadioButton>(R.id.rdServiceRegisterMonthlyInvoice)
        private lateinit var curTicket: MonthlyTicketType
        private var curPosition: Int? = null

        init {
            itemView.setOnClickListener {
                curPosition?.let { pos ->
                    chooseMonthlyTicket(pos)
                }
            }
        }

        fun bind(monthlyTicketType: MonthlyTicketType, position: Int) {
            curTicket = monthlyTicketType
            curPosition = position
            choose.isChecked = false
            container.setCardBackgroundColor(itemView.resources.getColor(R.color.white))
            choosePosition?.takeIf { curPosition == choosePosition }?.let {
                choose.isChecked = true
                container.setCardBackgroundColor(itemView.resources.getColor(R.color.light_gold))
            }
            time.text = "Gói dịch vụ ${curTicket.numberOfMonth.toInt()} tháng"
            description.text = curTicket.description
            oldPrice.text = "${FormatCurrencyUtil.formatNumberCeil(curTicket.originalPrice)} VND"
            newPrice.text = "${FormatCurrencyUtil.formatNumberCeil(curTicket.promotionalPrice)} VND"
        }
    }
}