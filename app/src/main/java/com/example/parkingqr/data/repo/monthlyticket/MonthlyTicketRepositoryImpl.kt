package com.example.parkingqr.data.repo.monthlyticket

import com.example.parkingqr.data.mapper.mapToMonthlyTicket
import com.example.parkingqr.data.mapper.mapToMonthlyTicketFirebase
import com.example.parkingqr.data.remote.State
import com.example.parkingqr.data.remote.monthlyticket.MonthlyTicketRemoteData
import com.example.parkingqr.domain.model.parkinglot.MonthlyTicket
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MonthlyTicketRepositoryImpl @Inject constructor(
    private val remoteData: MonthlyTicketRemoteData
) : MonthlyTicketRepository {
    override fun getCurrentUserMonthlyTicketList(): Flow<State<MutableList<MonthlyTicket>>> {
        return remoteData.getCurrentUserMonthlyTicketList().map { state ->
            when (state) {
                is State.Loading -> State.loading()
                is State.Success -> State.success(state.data.map { it.mapToMonthlyTicket() }
                    .toMutableList())
                is State.Failed -> State.failed(state.message)
            }
        }
    }

    override fun createMonthlyTicket(monthlyTicket: MonthlyTicket): Flow<State<Boolean>> {
        return remoteData.createMonthlyTicket(monthlyTicket.mapToMonthlyTicketFirebase())
            .map { state ->
                when (state) {
                    is State.Loading -> State.loading()
                    is State.Success -> State.success(state.data)
                    is State.Failed -> State.failed(state.message)
                }
            }
    }
}