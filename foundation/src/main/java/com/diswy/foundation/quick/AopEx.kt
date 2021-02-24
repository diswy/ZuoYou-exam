package com.diswy.foundation.quick

import android.view.View
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.delay

/**
 * kotlin协程导致热修复冲突，妥协关闭
 * 幽雅的过滤重复点击，默认500ms
 * @param time 需要过滤的时间，单位ms
 */
@UseExperimental(ObsoleteCoroutinesApi::class)
fun View.onClick(time: Long = 500L, action: suspend (View) -> Unit) {
    // launch one actor
    val eventActor = GlobalScope.actor<View>(Dispatchers.Main) {
        for (event in channel) {
            action(event)
            delay(time)
        }
    }
    // install a listener to activate this actor
    setOnClickListener {
        eventActor.offer(it)
    }
}