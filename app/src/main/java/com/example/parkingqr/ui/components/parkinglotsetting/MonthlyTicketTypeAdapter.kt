package com.example.parkingqr.ui.components.parkinglotsetting

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.parkingqr.R
import com.example.parkingqr.domain.model.parkinglot.MonthlyTicketType
import com.example.parkingqr.utils.FormatCurrencyUtil
import com.google.android.material.textfield.TextInputEditText
import java.lang.Integer.max

class MonthlyTicketTypeAdapter(
    private val list: MutableList<MonthlyTicketType>,
    private val onClickEdit: ((MonthlyTicketType) -> Unit),
    private val onClickAdd: ((MonthlyTicketType) -> Unit),
    private val onClickDel: ((MonthlyTicketType) -> Unit)
) :
    Adapter<MonthlyTicketTypeAdapter.MonthlyTicketTypeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonthlyTicketTypeViewHolder {
        return LayoutInflater.from(parent.context)
            .inflate(R.layout.item_monthly_ticket_setting, parent, false).let {
                MonthlyTicketTypeViewHolder(it)
            }
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: MonthlyTicketTypeViewHolder, position: Int) {
        holder.bind(list[position])
    }

    fun addAll(data: List<MonthlyTicketType>) {
        list.clear()
        list.addAll(data.toMutableList())
        notifyDataSetChanged()
    }

    fun add(data: MonthlyTicketType) {
        list.add(data)
        notifyItemRangeChanged(max(list.size - 2, 0), 1)
    }

    fun remove(monthlyTicketType: MonthlyTicketType) {
        list.remove(monthlyTicketType)
        notifyDataSetChanged()
    }

    inner class MonthlyTicketTypeViewHolder(itemView: View) : ViewHolder(itemView) {
        private val type = itemView.findViewById<TextView>(R.id.tvVehicleTypeMonthlyTicketSetting)
        private val vNumberOfMonth =
            itemView.findViewById<TextInputEditText>(R.id.edtNumOfMonthMonthlyTicketSetting)
        private val vOriginPrice =
            itemView.findViewById<TextInputEditText>(R.id.edtOriginalPriceMonthlyTicketSetting)
        private val vPromoPrice =
            itemView.findViewById<TextInputEditText>(R.id.edtPromoPriceMonthlyTicketSetting)

        private val vDescription =
            itemView.findViewById<TextInputEditText>(R.id.edtDescriptionMonthlyTicketSetting)

        private val edit = itemView.findViewById<ImageView>(R.id.ivEditMonthlyTicketSetting)

        private val del = itemView.findViewById<ImageView>(R.id.ivDeleteMonthlyTicketSetting)

        fun bind(monthlyTicketType: MonthlyTicketType) {
            initView(monthlyTicketType)
            initListener(monthlyTicketType)
        }

        private fun initView(monthlyTicketType: MonthlyTicketType) {
            type.text = monthlyTicketType.getReadableVehicleType()
            vNumberOfMonth.setText(FormatCurrencyUtil.formatNumberCeil(monthlyTicketType.numberOfMonth))
            vOriginPrice.setText(FormatCurrencyUtil.formatNumberCeil(monthlyTicketType.originalPrice))
            vPromoPrice.setText(FormatCurrencyUtil.formatNumberCeil(monthlyTicketType.promotionalPrice))
            vDescription.setText(monthlyTicketType.description)
            if (monthlyTicketType.id == MonthlyTicketType.DRAFT_ID_ITEM) {
                edit.setImageResource(R.drawable.create)
            } else {
                edit.setImageResource(R.drawable.edit)
            }
        }


        private fun initListener(monthlyTicketType: MonthlyTicketType) {
            initFormatEditText(vOriginPrice)
            initFormatEditText(vPromoPrice)
            // Not validate yet
            edit.setOnClickListener {
                val tmp = monthlyTicketType.apply {
                    numberOfMonth = vNumberOfMonth.text.toString().toDoubleOrNull() ?: 0.0
                    originalPrice =
                        FormatCurrencyUtil.convertFormatToNumber(vOriginPrice.text.toString())
                    promotionalPrice =
                        FormatCurrencyUtil.convertFormatToNumber(vPromoPrice.text.toString())
                    description = vDescription.text.toString()
                }
                if (monthlyTicketType.id != MonthlyTicketType.DRAFT_ID_ITEM) {
                    onClickEdit.invoke(tmp)
                } else {
                    onClickAdd.invoke(tmp)
                }
            }
            del.setOnClickListener {
                onClickDel.invoke(monthlyTicketType)
            }
        }

        private fun initFormatEditText(textInputEditText: TextInputEditText) {
            textInputEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    textInputEditText.removeTextChangedListener(this)
                    val formattedText = formatTextCurrency(s.toString())
                    textInputEditText.setText(formattedText)
                    textInputEditText.setSelection(formattedText.length)
                    textInputEditText.addTextChangedListener(this)
                }
            })
        }

        private fun formatTextCurrency(s: String): String {
            val cleanS = s.replace(",", "")
            return cleanS.toDoubleOrNull()?.let { number ->
                FormatCurrencyUtil.formatNumberCeil(number)
            } ?: ""
        }
    }
}