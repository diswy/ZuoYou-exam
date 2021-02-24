package com.diswy.foundation.tools

import com.diswy.foundation.tools.TimeKitJ.formatTime
import java.text.SimpleDateFormat
import java.util.*

/**
 * 时间转换小工具
 */
object TimeKit {

    fun full(timeMillis: Long): String {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeMillis
        return format.format(calendar.time)
    }

    fun YMDHM(time: String): String {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA)
        return format.format(Date(formatTime(time)))
    }
}