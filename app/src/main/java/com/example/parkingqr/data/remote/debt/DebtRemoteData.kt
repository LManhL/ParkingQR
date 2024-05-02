package com.example.parkingqr.data.remote.debt

import com.example.parkingqr.data.remote.State
import com.example.parkingqr.data.remote.dto.debt.InvoiceDebtFirebase
import com.example.parkingqr.data.remote.dto.invoice.ParkingInvoiceFirebase
import kotlinx.coroutines.flow.Flow

interface DebtRemoteData {
    fun getUserUnpaidDebtInvoice(): Flow<State<InvoiceDebtFirebase>>
    fun createDebtInvoice(parkingInvoiceFirebase: ParkingInvoiceFirebase): Flow<State<Boolean>>
    fun payDebtInvoice(invoiceDebtFirebase: InvoiceDebtFirebase): Flow<State<Boolean>>
}