package com.example.parkingqr.data.repo.debt

import com.example.parkingqr.data.mapper.mapToInvoiceDebt
import com.example.parkingqr.data.mapper.mapToParkingInvoiceFirebase
import com.example.parkingqr.data.remote.State
import com.example.parkingqr.data.remote.debt.DebtRemoteData
import com.example.parkingqr.domain.model.debt.InvoiceDebt
import com.example.parkingqr.domain.model.invoice.ParkingInvoice
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DebtRepositoryImpl @Inject constructor(
    private val remoteData: DebtRemoteData
) : DebtRepository {
    override fun getUserUnpaidDebtInvoice(): Flow<State<List<InvoiceDebt>>> {
        return remoteData.getUserUnpaidDebtInvoice().map { state ->
            when (state) {
                is State.Loading -> State.loading()
                is State.Success -> State.success(state.data.map { it.mapToInvoiceDebt() })
                is State.Failed -> State.failed(state.message)
            }
        }
    }

    override fun createDebtInvoice(parkingInvoice: ParkingInvoice): Flow<State<Boolean>> {
        return remoteData.createDebtInvoice(parkingInvoice.mapToParkingInvoiceFirebase())
    }

}