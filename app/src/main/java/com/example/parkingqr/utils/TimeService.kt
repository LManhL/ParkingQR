package com.example.parkingqr.utils

import android.os.Build
import android.util.Log
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.*
import java.util.*

object TimeService {
    const val HOUR_TO_MILISECONDS: Long = 3600000
    const val MINUTE_TO_MILISECONDS: Long = 60000

    fun convertToLocalTime(systemTime: Long, offSet: Long): Long {
        return systemTime + HOUR_TO_MILISECONDS * offSet
    }

    fun convertMilisecondsToDate(miliseconds: Long): String {
        val res = Date(miliseconds)
        val dateFormat: DateFormat = SimpleDateFormat("HH:mm dd/MM/yyyy")
        return dateFormat.format(res)
    }

    fun getCurrentTime(): Long {
        return System.currentTimeMillis()
    }

    fun getDateCurrentTime(): String{
        return convertMilisecondsToDate(getCurrentTime())
    }

    fun convertToMilliseconds(timeString: String?): Long {
        val format = "dd/MM/yyyy HH:mm:ss"
        var milliseconds: Long = 0
        try {
            val sdf = SimpleDateFormat(format)
            val date = sdf.parse(timeString)
            milliseconds = date.time
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return milliseconds
    }
}