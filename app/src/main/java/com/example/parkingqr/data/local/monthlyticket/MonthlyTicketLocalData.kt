package com.example.parkingqr.data.local.monthlyticket

interface MonthlyTicketLocalData {
    fun setIsShowMonthlyTicket(isShow: Boolean)
    fun getIsShowMonthlyTicket(): Boolean
    fun getSelectedMonthlyTicketId(): String?
    fun setSelectedMonthlyTicketId(monthlyTicketId: String)
}