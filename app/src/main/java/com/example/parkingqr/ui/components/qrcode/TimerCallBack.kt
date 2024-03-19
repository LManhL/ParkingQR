package com.example.parkingqr.ui.components.qrcode

interface TimerCallBack {
    fun onFinish()
    fun onWorking(count: Int)
}