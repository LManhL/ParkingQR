package com.example.parkingqr.data.local.monthlyticket

import android.content.Context
import com.example.parkingqr.data.local.user.UserLocalDataSource
import javax.inject.Inject

class MonthlyTicketLocalDataSource @Inject constructor(val context: Context): MonthlyTicketLocalData{
    companion object {
        const val SHARED_PREFERENCES_FILE_NAME = "monthly_ticket_shared_preferences"
        const val MONTHLY_TICKET_ID = "MONTHLY_TICKET_ID"
        const val IS_SHOW_MONTHLY_TICKET = "IS_SHOW_MONTHLY_TICKET"
    }

    private val sharedPref = context.getSharedPreferences(SHARED_PREFERENCES_FILE_NAME, 0)

    override fun setIsShowMonthlyTicket(isShow: Boolean) {
        val editor = sharedPref.edit()
        editor.putBoolean(IS_SHOW_MONTHLY_TICKET, isShow)
        editor.apply()
    }

    override fun getIsShowMonthlyTicket(): Boolean {
        return sharedPref.getBoolean(IS_SHOW_MONTHLY_TICKET, false)
    }

    override fun getSelectedMonthlyTicketId(): String? {
        return sharedPref.getString(MONTHLY_TICKET_ID, null)
    }

    override fun setSelectedMonthlyTicketId(monthlyTicketId: String) {
        val editor = sharedPref.edit()
        editor.putString(MONTHLY_TICKET_ID, monthlyTicketId)
        editor.apply()
    }
}