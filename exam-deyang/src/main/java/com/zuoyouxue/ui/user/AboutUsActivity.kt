package com.zuoyouxue.ui.user

import androidx.activity.viewModels
import com.diswy.foundation.base.activity.BaseToolbarBindActivity
import com.ebd.common.App
import com.ebd.common.config.Ebd
import com.ebd.common.viewmodel.ToolViewModel
import com.zuoyouxue.R
import com.zuoyouxue.databinding.ActivityAboutUsBinding
import com.zuoyouxue.ui.EbdWebActivity

class AboutUsActivity : BaseToolbarBindActivity<ActivityAboutUsBinding>() {

    private val toolVm: ToolViewModel by viewModels { App.instance.factory }

    override fun pageTitle(): String = "关于"

    override fun getLayoutRes(): Int = R.layout.activity_about_us

    override fun initialize() {
        binding.aboutVersion.text = "版本号：%s".format(toolVm.getAppInfo().versionName())
    }

    override fun bindListener() {
        binding.aboutPrivacyAgreement.setOnClickListener {
            EbdWebActivity.start(this, Ebd.AGGREMENT, "隐私协议")
        }
    }
}
