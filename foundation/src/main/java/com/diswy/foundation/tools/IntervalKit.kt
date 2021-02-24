package com.diswy.foundation.tools

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import io.reactivex.Flowable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 * 定时任务，绑定生命周期
 */
class IntervalKit : LifecycleObserver {

    private var duration: Int = 0
    private var intervalTask: Disposable? = null

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    private fun lifeOnPause() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private fun lifeOnStop() {
        stop()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun lifeOnDestroy() {

    }


    /**
     * @param intervalTime 间隔时间
     */
    fun start(intervalTime: Long = 1) {
        stop()
        intervalTask = Flowable.interval(intervalTime, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe({
                Timber.d("=========>>>自：${it},记录时长${duration}")
                duration += intervalTime.toInt()
            }, {

            })
    }

    fun stop() {
        intervalTask?.dispose()
    }

    fun setCurrentDuration(mStart: Int) {
        duration = mStart
    }

    fun getDuration(): Int {
        return duration
    }
}