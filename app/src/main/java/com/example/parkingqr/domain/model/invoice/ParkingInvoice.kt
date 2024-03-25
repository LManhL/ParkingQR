package com.example.parkingqr.domain.model.invoice

import com.example.parkingqr.data.remote.dto.invoice.ParkingInvoiceFirebase
import com.example.parkingqr.domain.model.vehicle.VehicleInvoice
import com.example.parkingqr.utils.TimeUtil


class ParkingInvoice() {

    companion object {
        const val PARKING_STATE_TYPE = "parking"
        const val PARKED_STATE_TYPE = "parked"
        const val REFUSED_STATE_TYPE = "refused"
        const val CASH_PAYMENT_METHOD = "cash"
        const val VNPAY_PAYMENT_METHOD = "vnpay"
        const val HOUR_INVOICE_TYPE = "hourType"
        const val MONTH_INVOICE_TYPE = "monthType"
    }

    var id: String = ""
    var user: UserInvoice = UserInvoice()
    var vehicle: VehicleInvoice = VehicleInvoice()
    var state: String = ""
    var imageIn: String = ""
    var imageOut: String = ""
    var timeOut: String = ""
    var price: Double = 0.0
    var timeIn: String = ""
    var paymentMethod: String = ""
    var type: String = ""
    var note: String = ""
    var parkingLotId: String = ""

    constructor(
        id: String,
        user: UserInvoice,
        vehicle: VehicleInvoice,
        imageIn: String,
        timeIn: String
    ) : this() {
        this.id = id
        this.user = user
        this.vehicle = vehicle
        this.state = PARKING_STATE_TYPE
        this.imageIn = imageIn
        this.price = 0.0
        this.timeIn = timeIn
        this.paymentMethod = CASH_PAYMENT_METHOD
        this.type = HOUR_INVOICE_TYPE
    }

    constructor(
        id: String,
        user: UserInvoice,
        vehicle: VehicleInvoice,
        state: String,
        imageIn: String,
        imageOut: String,
        timeOut: String,
        price: Double,
        timeIn: String,
        paymentMethod: String,
        type: String,
        note: String,
        parkingLotId: String
    ) : this() {
        this.id = id
        this.user = user
        this.vehicle = vehicle
        this.state = state
        this.imageIn = imageIn
        this.imageOut = imageOut
        this.price = price
        this.timeIn = timeIn
        this.timeOut = timeOut
        this.paymentMethod = paymentMethod
        this.type = type
        this.note = note
        this.parkingLotId = parkingLotId
    }

    constructor(parkingInvoiceFirebase: ParkingInvoiceFirebase) : this() {
        this.id = parkingInvoiceFirebase.id.toString()
        this.user = parkingInvoiceFirebase.user?.let { UserInvoice(it) }!!
        this.vehicle = parkingInvoiceFirebase.vehicle?.let { VehicleInvoice(it) }!!
        this.state = parkingInvoiceFirebase.state.toString()
        this.imageIn = parkingInvoiceFirebase.imageIn.toString()
        this.imageOut = parkingInvoiceFirebase.imageOut.toString()
        this.price = parkingInvoiceFirebase.price ?: 0.0
        this.timeIn = parkingInvoiceFirebase.timeIn.toString()
        this.timeOut = parkingInvoiceFirebase.timeOut.toString()
        this.paymentMethod = parkingInvoiceFirebase.paymentMethod.toString()
        this.type = parkingInvoiceFirebase.type.toString()
        this.note = parkingInvoiceFirebase.note.toString()
    }

    fun calTotalPrice(): Double {

        val endTime: Long = if (getState() == ParkingState.PARKING) {
            TimeUtil.getCurrentTime()
        } else {
            if (timeOut.isNotEmpty()) timeOut.toLong()
            else TimeUtil.getCurrentTime()
        }
        val hoursParked = ((endTime - timeIn.toLong()) / (1000 * 60 * 60)).toInt()
        var parkingFee = 3000.0
        if (hoursParked > 5) {
            val additionalHours = hoursParked - 5
            parkingFee += additionalHours * 250
        }
        return parkingFee
    }

    fun getState(): ParkingState {
        return if (state == PARKING_STATE_TYPE) {
            ParkingState.PARKING
        } else if (state == PARKED_STATE_TYPE) {
            ParkingState.PARKED
        } else {
            ParkingState.REFUSED
        }
    }

    fun isOnlinePayment(): Boolean {
        return paymentMethod == VNPAY_PAYMENT_METHOD
    }

    fun getPaymentMethodReadable(): String {
        return if (paymentMethod == CASH_PAYMENT_METHOD) {
            "Tiền mặt"
        } else if (paymentMethod == VNPAY_PAYMENT_METHOD) {
            "VNPAY"
        } else ""
    }

    fun getInvoiceTypeReadable(): String {
        return if (type == HOUR_INVOICE_TYPE) {
            "Gửi theo giờ"
        } else if (type == MONTH_INVOICE_TYPE) {
            "Gửi theo tháng"
        } else ""
    }

    enum class ParkingState {
        PARKING, PARKED, REFUSED
    }
}