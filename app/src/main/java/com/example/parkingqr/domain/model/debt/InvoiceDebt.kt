package com.example.parkingqr.domain.model.debt

import com.example.parkingqr.domain.model.invoice.ParkingInvoice

class InvoiceDebt(
    val id: String = "",
    val createAt: String = "",
    val parkingInvoice: ParkingInvoice? = null,
    val status: DebtStatus = DebtStatus.UNPAID
) {

    fun isPaid(): Boolean = status == DebtStatus.PAID

    enum class DebtStatus {
        UNPAID, PAID
    }
}