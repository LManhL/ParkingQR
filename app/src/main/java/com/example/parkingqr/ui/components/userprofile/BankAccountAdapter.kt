package com.example.parkingqr.ui.components.userprofile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.parkingqr.R
import com.example.parkingqr.domain.model.payment.BankAccount

class BankAccountAdapter(private val list: MutableList<BankAccount>) :
    Adapter<BankAccountAdapter.BankAccountViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BankAccountViewHolder {
        return LayoutInflater.from(parent.context)
            .inflate(R.layout.item_bank_account, parent, false).let {
                BankAccountViewHolder(it)
            }
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: BankAccountViewHolder, position: Int) {
        list[position].let {
            holder.bind(it)
        }
    }

    inner class BankAccountViewHolder(itemView: View) : ViewHolder(itemView) {
        private val icon = itemView.findViewById<ImageView>(R.id.ivIconBankAccount)
        private val name = itemView.findViewById<TextView>(R.id.tvNameBankAccount)
        private val number = itemView.findViewById<TextView>(R.id.tvNumberBankAccount)

        init {
            itemView.setOnClickListener {

            }
        }

        fun bind(bankAccount: BankAccount) {
            name.text = bankAccount.bankCode
            number.text = bankAccount.cardNumber
        }
    }
}