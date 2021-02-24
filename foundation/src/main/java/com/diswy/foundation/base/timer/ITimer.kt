package com.diswy.foundation.base.timer

interface ITimer {
    fun onTime(second: Int)
    fun onTimeEnd()
}