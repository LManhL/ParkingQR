package com.example.parkingqr.data.repo.debt

import com.example.parkingqr.data.remote.State
import com.example.parkingqr.domain.model.debt.InvoiceDebt
import com.example.parkingqr.domain.model.invoice.ParkingInvoice
import kotlinx.coroutines.flow.Flow

interface DebtRepository {
    fun getUserUnpaidDebtInvoice(): Flow<State<InvoiceDebt>>
    fun createDebtInvoice(parkingInvoice: ParkingInvoice): Flow<State<Boolean>>
    fun payDebtInvoice(invoiceDebt: InvoiceDebt): Flow<State<Boolean>>
}