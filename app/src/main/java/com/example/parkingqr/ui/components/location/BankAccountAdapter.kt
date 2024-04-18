package com.example.parkingqr.ui.components.location

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

    private var onClick: ((BankAccount) -> Unit)? = null

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

    fun setOnClickItem(callBank: ((BankAccount) -> Unit)) {
        this.onClick = callBank
    }

    inner class BankAccountViewHolder(itemView: View) : ViewHolder(itemView) {
        private val icon = itemView.findViewById<ImageView>(R.id.ivIconBankAccount)
        private val name = itemView.findViewById<TextView>(R.id.tvNameBankAccount)
        private val number = itemView.findViewById<TextView>(R.id.tvNumberBankAccount)
        private lateinit var curBankAccount: BankAccount

        init {
            itemView.setOnClickListener {
                onClick?.invoke(curBankAccount)
            }
        }

        fun bind(bankAccount: BankAccount) {
            curBankAccount = bankAccount
            name.text = curBankAccount.bankCode
            number.text = curBankAccount.cardNumber
        }
    }
}