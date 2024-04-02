package com.example.parkingqr.data.remote.monthlyticket

import com.example.parkingqr.data.remote.State
import com.example.parkingqr.data.remote.dto.parkinglot.MonthlyTicketFirebase
import kotlinx.coroutines.flow.Flow

interface MonthlyTicketRemoteData {
    fun getCurrentUserMonthlyTicketList(): Flow<State<MutableList<MonthlyTicketFirebase>>>
    fun createMonthlyTicket(monthlyTicketFirebase: MonthlyTicketFirebase): Flow<State<Boolean>>
}