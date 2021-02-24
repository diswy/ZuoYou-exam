package com.zuoyouxue.ui.user

import android.app.AlertDialog
import android.content.Intent
import com.diswy.foundation.base.activity.BaseToolbarBindActivity
import com.diswy.foundation.tools.CleanCacheTool
import com.ebd.common.cache.CacheTool
import com.ebd.common.config.CacheKey
import com.ebd.common.config.EbdState
import com.just.agentweb.AgentWebConfig
import com.zuoyouxue.R
import com.zuoyouxue.databinding.ActivitySettingBinding

class SettingActivity : BaseToolbarBindActivity<ActivitySettingBinding>() {

    override fun pageTitle(): String = "设置"

    override fun getLayoutRes(): Int = R.layout.activity_setting

    override fun initialize() {
        binding.settingCacheLength.text = CleanCacheTool.getTotalCacheSize(this)
    }

    override fun bindListener() {
        binding.settingModifyPwd.setOnClickListener {
            startActivity(Intent(this, ModifyPasswordActivity::class.java))
        }

        binding.settingAboutUs.setOnClickListener {
            startActivity(Intent(this, AboutUsActivity::class.java))
        }

        binding.settingClearCache.setOnClickListener {
            AlertDialog.Builder(this).setMessage("你确定要清除缓存么？")
                .setPositiveButton("确定") { _, _ ->
                    AgentWebConfig.clearDiskCache(this)
                    CleanCacheTool.clearAllCache(this)
                    binding.settingCacheLength.text = CleanCacheTool.getTotalCacheSize(this)
                }
                .setNegativeButton("我再想想", null)
                .show()
        }

        binding.logOut.setOnClickListener {
            AlertDialog.Builder(this).setMessage("你确定要退出登录么？")
                .setPositiveButton("确定") { _, _ ->
                    CacheTool.setValue(CacheKey.USER to "")
                    setResult(EbdState.RELOAD)
                    finish()
                }
                .setNegativeButton("我再想想", null)
                .show()
        }
    }
}
