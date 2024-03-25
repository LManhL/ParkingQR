package com.example.parkingqr.data.local.user

import android.content.Context
import javax.inject.Inject

class UserLocalDataSource @Inject constructor(val context: Context) : UserLocalData {

    companion object {
        const val SHARED_PREFERENCES_FILE_NAME = "user_shared_preferences"
        const val PARKING_LOT_ID_KEY = "PARKING_LOT_ID_KEY"
    }

    private val sharedPref = context.getSharedPreferences(SHARED_PREFERENCES_FILE_NAME, 0)
    override fun saveParkingLotId(parkingLotId: String) {
        val editor = sharedPref.edit()
        editor.putString(PARKING_LOT_ID_KEY, parkingLotId)
        editor.apply()
    }

    override fun getParingLotId(): String? {
        return sharedPref.getString(PARKING_LOT_ID_KEY, "0")
    }
}