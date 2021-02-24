package com.zuoyouxue.ui

import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.diswy.foundation.base.activity.BaseBindActivity
import com.diswy.foundation.quick.md5
import com.diswy.foundation.web.Status
import com.ebd.common.App
import com.ebd.common.cache.loginId
import com.ebd.common.viewmodel.ToolViewModel
import com.zuoyouxue.R
import com.zuoyouxue.databinding.ActivityGankaoLibBinding
import java.util.*

/**
 * 第三方资源
 */
class GankaoLibActivity : BaseBindActivity<ActivityGankaoLibBinding>() {

    private val toolViewModel: ToolViewModel by viewModels { App.instance.factory }

    override fun statusDarkMode(): Boolean = true

    override fun getLayoutRes(): Int = R.layout.activity_gankao_lib

    override fun initialize() {
        lifecycle.addObserver(binding.ganWeb)

        toolViewModel.ganPer.observe(this, Observer {
            when (it.status) {
                Status.LOADING -> {
                }
                Status.SUCCESS -> {
                    if (it.data!!.Permission)
                        loadGankaoH5()
                    else
                        binding.ganPermission.visibility = View.VISIBLE
                }
                Status.ERROR -> {
                    handleExceptions(it.throwable)
                }
            }
        })

        toolViewModel.getGanPer()
    }

    override fun onBackPressed() {
        val agent = binding.ganWeb.getAgent()
        if (agent == null) {
            super.onBackPressed()
        } else if (!agent.back()) {
            super.onBackPressed()
        }
    }

    private fun loadGankaoH5() {
        val treeMap = TreeMap<String, String>()
        treeMap["app_id"] = "jiaoyudashuju"
        treeMap["timestamp"] = System.currentTimeMillis().toString()
        treeMap["unionId"] = loginId.toString()
        treeMap["sex"] = "0"
        val strBuffer = StringBuffer()
        val webUrl = StringBuffer()
        webUrl.append("https://www.gankao.com/partner/login?")
        for (item in treeMap.entries) {
            strBuffer.append(item.key)
            strBuffer.append("=")
            strBuffer.append(item.value)
            strBuffer.append("&")

            webUrl.append(item.key)
            webUrl.append("=")
            webUrl.append(item.value)
            webUrl.append("&")
        }
        strBuffer.deleteCharAt(strBuffer.length - 1)
        strBuffer.append("Lkidiquwe234PldJJkBB")

        val md5 = strBuffer.toString().md5()
        webUrl.append("token=")
        webUrl.append(md5)

        binding.ganWeb.load(this, webUrl.toString(), false)
    }

}
