package com.example.parkingqr.ui.components.qrcode

import kotlinx.coroutines.*

abstract class Timer(val timeDelay: Long, val limit: Int) : TimerCallBack {
    private var job: Job? = null
    private var count = 0

    fun start() {
        stop()
        job = CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                onWorking(count)
                delay(timeDelay)
                count++
                if (count % limit == 0) {
                    onFinish()
                    stop()
                }
            }
        }
    }

    abstract override fun onFinish()
    abstract override fun onWorking(count: Int)

    fun stop() {
        job?.cancel()
        count = 0
    }

}