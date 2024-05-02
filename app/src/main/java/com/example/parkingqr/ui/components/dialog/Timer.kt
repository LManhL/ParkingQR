package com.example.parkingqr.ui.components.dialog

import kotlinx.coroutines.*

abstract class Timer(private val timeDelay: Long, private val limit: Int) : TimerCallBack {

    companion object{
        const val SECOND_MILLISECONDS: Long = 1000
        const val MINUTE_MILLISECONDS: Long = 60000
        const val HOUR_MILLISECONDS: Long = 3600000
    }

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
    fun stop() {
        job?.cancel()
        count = 0
    }

}