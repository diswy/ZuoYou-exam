package com.zuoyouxue.ui.user

import com.alibaba.android.arouter.facade.annotation.Route
import com.diswy.foundation.base.activity.BaseActivity
import com.diswy.foundation.base.timer.ITimer
import com.ebd.common.Page
import com.zuoyouxue.R


@Route(path = Page.LOGIN)
class LoginActivity : BaseActivity() {

    override fun statusDarkMode(): Boolean = true

    override fun getLayoutRes(): Int = R.layout.activity_login

    override fun initialize() {}

    private var myITimer: ITimer? = null

    fun setCurrentTimer(iTimer: ITimer) {
        myITimer = iTimer
    }

    fun tickTok(time: Long) {
        timer(time, object : ITimer {
            override fun onTime(second: Int) {
                myITimer?.onTime(second)
            }

            override fun onTimeEnd() {
                myITimer?.onTimeEnd()
            }
        })
    }
}
