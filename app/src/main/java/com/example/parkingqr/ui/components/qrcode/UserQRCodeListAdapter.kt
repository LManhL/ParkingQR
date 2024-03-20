package com.example.parkingqr.ui.components.qrcode

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.parkingqr.R
import com.example.parkingqr.domain.model.invoice.ParkingInvoice
import com.example.parkingqr.domain.model.qrcode.InvoiceQRCode
import com.example.parkingqr.utils.AESEncyptionUtil
import com.example.parkingqr.utils.FormatCurrencyUtil
import com.example.parkingqr.utils.QRcodeUtil
import com.example.parkingqr.utils.TimeUtil

class UserQRCodeListAdapter(private val invoiceList: MutableList<ParkingInvoice>) :
    Adapter<UserQRCodeListAdapter.InvoiceViewHolder>() {

    private var onClickItem: ((ParkingInvoice) -> Unit)? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InvoiceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user_qrcode_list, parent, false)
        return InvoiceViewHolder(view)
    }

    override fun getItemCount(): Int {
        return invoiceList.size
    }

    override fun onBindViewHolder(holder: InvoiceViewHolder, position: Int) {
        holder.bind(invoiceList[position])
    }

    fun setEventClick(callback: ((ParkingInvoice) -> Unit)) {
        onClickItem = callback
    }

    inner class InvoiceViewHolder(itemView: View) : ViewHolder(itemView) {

        private val licensePlate: TextView =
            itemView.findViewById(R.id.tvLicensePlateUserQRCodeList)
        private val timeIn: TextView = itemView.findViewById(R.id.tvTimeInUserQRCodeList)
        private val qrCodeImage: ImageView = itemView.findViewById(R.id.ivQRUserQRCodeList)
        private val chooseCash: RadioButton = itemView.findViewById(R.id.rdCashUserQrCodeList)
        private val chooseOnlinePayment: RadioButton =
            itemView.findViewById(R.id.rdOnlinePaymentUserQrCodeList)
        private val containerCash: CardView = itemView.findViewById(R.id.crdCashUserQRCodeList)
        private val containerOnlinePayment: CardView =
            itemView.findViewById(R.id.crdOnlinePaymentUserQRCodeList)
        private lateinit var curInvoice: ParkingInvoice

        init {
            itemView.setOnClickListener {
                onClickItem?.invoke(curInvoice)
            }
            chooseCash.setOnClickListener {
                handleChooseCash()
            }
            chooseOnlinePayment.setOnClickListener {
                handleChooseOnlinePayment()
            }
        }

        fun bind(invoice: ParkingInvoice) {
            curInvoice = invoice
            licensePlate.text = "${invoice.vehicle.licensePlate}"
            timeIn.text = "Thời gian vào là ${TimeUtil.convertMilisecondsToDate(invoice.timeIn)}"
            AESEncyptionUtil.encrypt(
                InvoiceQRCode(
                    invoiceId = invoice.id,
                    timeOut = TimeUtil.getDateCurrentTime()
                ).toString()
            )?.let {
                qrCodeImage.setImageBitmap(QRcodeUtil.getQrCodeBitmap(it))
            }
        }

        private fun handleChooseCash() {
            containerCash.setCardBackgroundColor(itemView.resources.getColor(R.color.light_orange))
            containerOnlinePayment.setCardBackgroundColor(itemView.resources.getColor(R.color.white))
            chooseCash.isChecked = true
            chooseOnlinePayment.isChecked = false
        }

        private fun handleChooseOnlinePayment() {
            containerOnlinePayment.setCardBackgroundColor(itemView.resources.getColor(R.color.light_orange))
            containerCash.setCardBackgroundColor(itemView.resources.getColor(R.color.white))
            chooseCash.isChecked = false
            chooseOnlinePayment.isChecked = true
        }
    }
}