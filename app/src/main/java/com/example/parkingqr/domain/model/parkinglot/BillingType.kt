package com.example.parkingqr.domain.model.parkinglot

import com.example.parkingqr.utils.TimeUtil
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class BillingType(
    var id: String = "",
    var firstBlockPrice: Double = 0.0,
    var afterFirstBlockPrice: Double = 0.0,
    var firstBlock: Double = 0.0,
    var roundedMinutesToOneHour: Double = 0.0,
    var nightSurcharge: Double = 0.0,
    var startDaylightTime: String = "",
    var endDaylightTime: String = "",
    var startNightTime: String = "",
    var endNightTime: String = "",
    var type: String = "",
    var vehicleType: String = "",
    var surcharge: Double = 0.0
) {
    companion object {
        const val HOUR_INVOICE_TYPE = "hourType"
        const val MONTH_INVOICE_TYPE = "monthType"
        const val CAR_TYPE = "car"
        const val MOTORBIKE_TYPE = "motorbike"
        const val OTHER_TYPE = "other"
        const val CAR_TYPE_READABLE = "Xe hơi"
        const val MOTORBIKE_TYPE_READABLE = "Xe máy"
        const val OTHER_TYPE_READABLE = "Xe khác"
    }


    fun calculateInvoicePrice(timeIn: String, timeOut: String): Double {
        val formatHourMinutes: DateFormat = SimpleDateFormat("HH:mm")
        val formatDate: DateFormat = SimpleDateFormat("MM/dd/yyyy")
        val timeInDate = Date(timeIn.toLong())
        val timeOutDate = Date(timeOut.toLong())

        var total = 0.0
        val totalTimeInMinutes =
            ((timeOutDate.time - timeInDate.time) / 1000 / TimeUtil.MINUTES_TO_HOUR).toDouble()
        total += firstBlockPrice + ((totalTimeInMinutes - firstBlock * TimeUtil.MINUTES_TO_HOUR) / TimeUtil.MINUTES_TO_HOUR).toInt() * afterFirstBlockPrice
        if (totalTimeInMinutes % TimeUtil.MINUTES_TO_HOUR > roundedMinutesToOneHour) total += afterFirstBlockPrice

        val hourMinuteTimeIn = formatHourMinutes.format(timeInDate)
        val hourMinuteTimeOut = formatHourMinutes.format(timeOutDate)
        val isTimeInCollapsedNightTime = TimeUtil.isTimeAfter(endNightTime, hourMinuteTimeIn)
        val isTimeOutCollapsedNightTime = TimeUtil.isTimeAfter(hourMinuteTimeOut, startNightTime)

        // Thời gian gửi ko dính gì đến đêm
        if (!isTimeInCollapsedNightTime && !isTimeOutCollapsedNightTime) {
            total += 0.0
        }
        // Thời gian cả hai đầu đều dính đến đêm
        else if (isTimeInCollapsedNightTime && isTimeOutCollapsedNightTime) {
            total += nightSurcharge * 2
        }
        // Thời gian 1 trong 2 đầu dính đến đêm
        else {
            total += nightSurcharge
        }
        formatDate.parse(formatDate.format(timeOutDate))?.let { outDate ->
            formatDate.parse(formatDate.format(timeOutDate))?.let { inDate ->
                outDate.time - inDate.time
            }
        }?.let { milliseconds ->
            TimeUnit.DAYS.convert(milliseconds, TimeUnit.MILLISECONDS).let { diff ->
                total += diff * nightSurcharge
            }
        }
        return total
    }

    fun getVehicleTypeReadable(): String =
        if (vehicleType == MOTORBIKE_TYPE) MOTORBIKE_TYPE_READABLE else if (vehicleType == CAR_TYPE) CAR_TYPE_READABLE else OTHER_TYPE_READABLE
}