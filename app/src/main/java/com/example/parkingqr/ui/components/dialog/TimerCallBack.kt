package com.example.parkingqr.ui.components.dialog

interface TimerCallBack {
    fun onFinish()
    fun onWorking(count: Int)
}