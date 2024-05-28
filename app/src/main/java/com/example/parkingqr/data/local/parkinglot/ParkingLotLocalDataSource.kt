package com.example.parkingqr.data.local.parkinglot

import android.content.Context
import javax.inject.Inject

class ParkingLotLocalDataSource @Inject constructor(context: Context) : ParkingLotLocalData {

    companion object {
        const val SHARED_PREFERENCES_FILE_NAME = "parking_lot_shared_preferences"
        const val URI_CAMERA_IN = "uri_camera_in"
        const val URI_CAMERA_OUT = "uri_camera_out"
    }

    private val sharedPref = context.getSharedPreferences(SHARED_PREFERENCES_FILE_NAME, 0)

    override fun setUriCameraIn(uri: String) {
        val editor = sharedPref.edit()
        editor.putString(URI_CAMERA_IN, uri)
        editor.apply()
    }

    override fun setUriCameraOut(uri: String) {
        val editor = sharedPref.edit()
        editor.putString(URI_CAMERA_OUT, uri)
        editor.apply()
    }

    override fun getUriCameraIn(): String? {
        return sharedPref.getString(URI_CAMERA_IN, null)
    }

    override fun getUriCameraOut(): String? {
        return sharedPref.getString(URI_CAMERA_OUT, null)
    }
}