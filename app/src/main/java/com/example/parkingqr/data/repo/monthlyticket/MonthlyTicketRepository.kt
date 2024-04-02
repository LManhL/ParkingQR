package com.example.parkingqr.data.repo.monthlyticket

import com.example.parkingqr.data.remote.State
import com.example.parkingqr.domain.model.parkinglot.MonthlyTicket
import kotlinx.coroutines.flow.Flow

interface MonthlyTicketRepository {
    fun getCurrentUserMonthlyTicketList(): Flow<State<MutableList<MonthlyTicket>>>
    fun createMonthlyTicket(monthlyTicket: MonthlyTicket): Flow<State<Boolean>>
}