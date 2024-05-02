package com.example.parkingqr.data.mapper

import com.example.parkingqr.data.remote.dto.debt.InvoiceDebtFirebase
import com.example.parkingqr.domain.model.debt.InvoiceDebt

fun InvoiceDebtFirebase.mapToInvoiceDebt(): InvoiceDebt {
    return InvoiceDebt(
        id = id ?: "",
        createAt = createAt ?: "",
        status = if (status == InvoiceDebtFirebase.STATUS_UN_PAID) InvoiceDebt.DebtStatus.UNPAID else InvoiceDebt.DebtStatus.PAID,
        parkingInvoice = parkingInvoice?.mapToParkingInvoice()
    )
}

fun InvoiceDebt.mapToInvoiceDebtFirebase(): InvoiceDebtFirebase {
    return InvoiceDebtFirebase(
        id = id ?: "",
        createAt = createAt ?: "",
        status = if (status == InvoiceDebt.DebtStatus.UNPAID) InvoiceDebtFirebase.STATUS_UN_PAID else InvoiceDebtFirebase.STATUS_PAID,
        parkingInvoice = parkingInvoice?.mapToParkingInvoiceFirebase()
    )
}