package com.example.parkingqr.ui.components.location

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.example.parkingqr.R
import com.example.parkingqr.domain.model.parkinglot.MonthlyTicket
import com.example.parkingqr.utils.FormatCurrencyUtil

class MonthlyTicketAdapter(private val list: MutableList<MonthlyTicket>) :
    Adapter<MonthlyTicketAdapter.MonthlyTicketViewHolder>() {

    private var choosePosition: Int? = null

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
        holder.bind(value)
    }

    fun chooseMonthlyTicket(position: Int) {
        val oldPos = choosePosition
        choosePosition = position
        oldPos?.let {
            notifyItemChanged(it)
        }
    }

    inner class MonthlyTicketViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val container =
            itemView.findViewById<LinearLayout>(R.id.cdvContainerRegisterMonthlyInvoice)
        private val time = itemView.findViewById<TextView>(R.id.tvTimeRegisterMonthlyInvoice)
        private val description =
            itemView.findViewById<TextView>(R.id.tvDescriptionRegisterMonthlyInvoice)
        private val oldPrice =
            itemView.findViewById<TextView>(R.id.tvOldPriceRegisterMonthlyInvoice)
        private val newPrice =
            itemView.findViewById<TextView>(R.id.tvNewPriceRegisterMonthlyInvoice)
        private val choose =
            itemView.findViewById<RadioButton>(R.id.rdServiceRegisterMonthlyInvoice)
        private lateinit var curTicket: MonthlyTicket
        private var curPosition: Int? = null

        init {
            container.setOnClickListener {
                curPosition?.let { pos ->
                    chooseMonthlyTicket(pos)
                }
            }
        }

        fun bind(monthlyTicket: MonthlyTicket) {
            curTicket = monthlyTicket
            curPosition = adapterPosition
            time.text = "Gói dịch vụ ${curTicket.numberOfMonth.toInt()} tháng"
            description.text = curTicket.description
            oldPrice.text = "${FormatCurrencyUtil.formatNumberCeil(curTicket.originalPrice)} VND"
            newPrice.text = "${FormatCurrencyUtil.formatNumberCeil(curTicket.promotionalPrice)} VND"
            choose.isChecked = false
            choosePosition?.takeIf { curPosition == choosePosition }?.let {
                choose.isChecked = true
            }
        }
    }
}