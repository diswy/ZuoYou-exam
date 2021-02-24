package com.zuoyouxue.ui

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.text.method.ScrollingMovementMethod
import android.view.View
import androidx.activity.viewModels
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.diswy.foundation.base.activity.BaseBindActivity
import com.diswy.foundation.base.timer.ITimer
import com.diswy.foundation.tools.ACache
import com.diswy.foundation.tools.GlideApp
import com.diswy.foundation.view.dialog.FancyDialogFragment
import com.diswy.foundation.web.Status
import com.ebd.common.App
import com.ebd.common.Page
import com.ebd.common.cache.CacheTool
import com.ebd.common.config.CacheKey
import com.ebd.common.viewmodel.CommonViewModel
import com.ebd.common.vo.Update
import com.ebd.common.vo.WebConfig
import com.zuoyouxue.R
import com.zuoyouxue.databinding.ActivitySplashBinding
import com.zuoyouxue.databinding.DialogUpdateBinding


class SplashActivity : BaseBindActivity<ActivitySplashBinding>(), ITimer {

    private val cvm: CommonViewModel by viewModels { App.instance.factory }

    override fun statusDarkMode(): Boolean = true

    override fun getLayoutRes(): Int = R.layout.activity_splash

    override fun initialize() {
        cvm.setting.observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    initConfig(it.data!!)
                }
                Status.LOADING -> {
                }
                Status.ERROR -> {
                    handleExceptions(it.throwable)
                    next()
//                    binding.btnSkip.visibility = View.VISIBLE
//                    timer(2, this)
                }
            }
        })
        cvm.getConfig()
    }

    @SuppressLint("SetTextI18n")
    override fun onTime(second: Int) {
        binding.btnSkip.text = "跳过 ${second + 1} 秒"
    }

    override fun onTimeEnd() {
        next()
    }

    override fun bindListener() {
        binding.btnSkip.setOnClickListener {
            next()
        }
    }

    /**
     * 未登录跳转登录，已登录跳转首页
     */
    private fun next() {
        val compat = ActivityOptionsCompat
            .makeCustomAnimation(this, R.anim.ebd_fade_in, R.anim.ebd_fade_out)

//        val mCache = ACache.get(this)
//        val account: String? = mCache.getAsString(CacheKey.TMP_USER)

        ARouter.getInstance().build(Page.LOGIN)
            .withOptionsCompat(compat)
            .navigation()

//        if (account.isNullOrEmpty()) {// 跳转登录页
//            ARouter.getInstance().build(Page.LOGIN)
//                .withOptionsCompat(compat)
//                .navigation()
//        } else {// 可以继续作答
//            ARouter.getInstance().build(Page.INFO)
//                .withOptionsCompat(compat)
//                .navigation()
//        }


//        if (getUser() == null) {
//            ARouter.getInstance().build(Page.LOGIN)
//                .withOptionsCompat(compat)
//                .navigation()
//        } else {
//            ARouter.getInstance().build(Page.HOME)
//                .withOptionsCompat(compat)
//                .navigation()
//        }
        finish()
    }

    /**
     * 动态可控图加载
     */
    private fun loadPic(url: String) {
        GlideApp.with(this)
            .asDrawable()
            .load(url)
            .into(object : CustomTarget<Drawable>() {
                override fun onLoadCleared(placeholder: Drawable?) {
                }

                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    setTheme(R.style.AppTheme)
                    binding.imgNews.setImageDrawable(resource)
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    setTheme(R.style.AppTheme)
                    val bg = ContextCompat.getDrawable(this@SplashActivity, R.drawable.bg_splash)
                    window.setBackgroundDrawable(bg)
                }
            })
    }

    /**
     * 处理配置
     */
    private fun initConfig(data: String) {
        try {
            val setting: WebConfig = App.instance.globalGson.fromJson(data, WebConfig::class.java)
            CacheTool.setValue(CacheKey.COEFF to setting.soeCoeff)
            loadPic(setting.launchUrl)
            next()
        } catch (e: Exception) {
            next()
        }
    }

    /**
     * 检查更新
     */
    private fun checkUpdate(update: Update) {
        val curVersionCode = cvm.getAppInfo().versionCode()
        if (curVersionCode < update.versionCode) {// 需要更新
            showUpdateDialog(update)
        } else {// 不需要更新
            binding.btnSkip.visibility = View.VISIBLE
            timer(2, this)
        }
    }

    /**
     * 更新弹出框
     */
    private fun showUpdateDialog(update: Update) {
        val dialog = FancyDialogFragment.create()
            .setLayoutRes(R.layout.dialog_update)
            .setViewListener { dialog, dialogBinding ->
                dialogBinding as DialogUpdateBinding
                dialogBinding.updateInfo.movementMethod = ScrollingMovementMethod.getInstance()
                if (update.forceUpdate) {// 强制更新
                    dialogBinding.updateGap.visibility = View.GONE
                    dialogBinding.updateCancel.visibility = View.GONE
                }
                dialogBinding.updateTitle.text = "发现新版本啦！V%s".format(update.versionName)
                dialogBinding.updateInfo.text = update.description
                dialogBinding.updateCancel.setOnClickListener {
                    dialog.dismiss()
                    next()
                }
                dialogBinding.updateConfirm.setOnClickListener {
                    dialog.dismiss()
                    cvm.update(this, update.downloadUrl, update.versionName, "下载中...")
                }
            }
            .setWidth(this, 300)
            .setCanCancelOutside(false)
        dialog.show(supportFragmentManager, "update")
    }
}
