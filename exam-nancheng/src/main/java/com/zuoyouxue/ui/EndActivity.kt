package com.zuoyouxue.ui

import android.view.KeyEvent
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.diswy.foundation.base.activity.BaseActivity
import com.diswy.foundation.quick.toast
import com.diswy.foundation.web.Status
import com.ebd.common.App
import com.ebd.common.viewmodel.CommonViewModel
import com.zuoyouxue.R
import kotlin.system.exitProcess

class EndActivity : BaseActivity() {

    private val cvm: CommonViewModel by viewModels { App.instance.factory }

    override fun fullScreenMode(): Boolean = true

    override fun getLayoutRes(): Int = R.layout.activity_end

    override fun initialize() {
//        cvm.ncUrl.observe(this, Observer {
//            when (it.status) {
//                Status.SUCCESS -> {
//                    it.data?.let { url ->
//                        EbdWebActivity.start(this, url)
//                    }
//                }
//                Status.LOADING -> {
//                }
//                Status.ERROR -> {
//                    handleExceptions(it.throwable)
//                }
//            }
//        })
//        cvm.getHeartQuestion()
    }

    private var exitTime: Long = 0
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                toast("再按一次即将退出应用")
                exitTime = System.currentTimeMillis()
            } else {
                finish()
                exitProcess(0)
            }
            return true
        }

        return super.onKeyDown(keyCode, event)
    }

}