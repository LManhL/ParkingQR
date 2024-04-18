package com.example.parkingqr.data.remote.dto.debt

import com.example.parkingqr.data.remote.dto.invoice.ParkingInvoiceFirebase

data class InvoiceDebtFirebase(
    val id: String? = null,
    val createAt: String? = null,
    val parkingInvoiceFirebase: ParkingInvoiceFirebase? = null,
    val status: String? = null
){
    companion object{
        const val STATUS_UN_PAID = "unpaid"
        const val STATUS_PAID = "paid"
    }
}