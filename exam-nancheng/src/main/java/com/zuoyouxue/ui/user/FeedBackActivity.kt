package com.zuoyouxue.ui.user

import android.text.TextUtils
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.diswy.foundation.base.activity.BaseToolbarBindActivity
import com.diswy.foundation.quick.toast
import com.diswy.foundation.web.Resource
import com.diswy.foundation.web.Status
import com.ebd.common.App
import com.ebd.common.cache.loginId
import com.ebd.common.config.Ebd
import com.ebd.common.viewmodel.CommonViewModel
import com.ebd.common.vo.BaseResponse
import com.zuoyouxue.R
import com.zuoyouxue.databinding.ActivityFeedBackBinding
import com.zuoyouxue.ui.EbdWebActivity

class FeedBackActivity : BaseToolbarBindActivity<ActivityFeedBackBinding>() {

    private val cvm: CommonViewModel by viewModels { App.instance.factory }
    private val observe: Observer<Resource<BaseResponse<Unit>>> = Observer {
        when (it.status) {
            Status.LOADING -> {
            }
            Status.SUCCESS -> {
                if (it.data!!.isSuccess) {
                    toast("亲，反馈成功了呢，请您耐心等待客服的回复哦！")
                    binding.feedEt.setText("")
                } else {
                    toast(it.data!!.message)
                }
            }
            Status.ERROR -> {
                handleExceptions(it.throwable)
            }
        }
    }

    override fun pageTitle(): String = "问题反馈"

    override fun getLayoutRes(): Int = R.layout.activity_feed_back

    override fun initialize() {
        mToolbar.inflateMenu(R.menu.feed_back)

        cvm.feedBack.observe(this, observe)
    }

    override fun bindListener() {
        mToolbar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.look_feed) {
                val url = Ebd.URL_FEED_BACK.format(loginId)
                EbdWebActivity.start(this, url, "查看回复")
            }
            return@setOnMenuItemClickListener false
        }
        binding.feedCommit.setOnClickListener {
            if (check()) {
                var type = ""
                when (binding.feedRg.checkedRadioButtonId) {
                    R.id.type_question -> type = "问题"
                    R.id.type_advise -> type = "建议"
                    R.id.type_other -> type = "其他"
                }
                cvm.feedBack(binding.feedEt.text.toString().trim(), type)
            }
        }
    }

    //验证输入的内容是否为空
    private fun check(): Boolean {
        if (TextUtils.isEmpty(binding.feedEt.text.toString().trim { it <= ' ' })) {
            toast("请输入反馈内容")
            return false
        }
        return true
    }

}
