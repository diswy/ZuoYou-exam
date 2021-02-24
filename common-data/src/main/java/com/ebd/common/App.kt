package com.ebd.common

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import com.alibaba.android.arouter.launcher.ARouter
import com.diswy.foundation.quick.toast
import com.ebd.common.di.AppComponent
import com.ebd.common.di.DaggerAppComponent
import com.ebd.common.widget.CustomAttachParser
import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.msg.MsgService
import com.netease.nimlib.sdk.util.NIMUtil
import com.umeng.analytics.MobclickAgent
import com.umeng.commonsdk.UMConfigure
import io.reactivex.plugins.RxJavaPlugins
import org.json.JSONException
import retrofit2.HttpException
import timber.log.Timber
import timber.log.Timber.DebugTree
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.text.ParseException
import java.util.concurrent.CancellationException
import javax.inject.Inject

class App : Application() {

    companion object {
        lateinit var instance: App
            private set

        lateinit var appComponent: AppComponent
            private set
    }

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    @Inject
    lateinit var globalGson: Gson

    override fun onCreate() {
        super.onCreate()
        instance = this
        appComponent = DaggerAppComponent.builder().application(this).build()
        appComponent.inject(this)
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
            ARouter.openLog()
            ARouter.openDebug()
        }
        ARouter.init(this)
        UMConfigure.init(this, UMConfigure.DEVICE_TYPE_PHONE, null)
        // RX错误捕获，发送至友盟自定义错误类型
        RxJavaPlugins.setErrorHandler {
            MobclickAgent.reportError(this, it)
        }

        // 网易云信
        // 每次都需要登录，这里不是用缓存登录getNeteaseLoginInfo()
        NIMClient.init(this, null, null)
        if (NIMUtil.isMainProcess(this)) {
            // 注册自定义消息附件解析器
            NIMClient.getService(MsgService::class.java)
                .registerCustomAttachmentParser(CustomAttachParser())
        }
    }

    fun handleExceptions(t: Throwable?) {
        if (t == null) {
            toast("未知错误")
            return
        }
        if (t is CancellationException) {
            return
        }
        val errorMessage = when (t) {
            is SocketException -> "网络异常，请检查网络后重试"
            is SocketTimeoutException -> "网络超时,请稍后尝试"
            is UnknownHostException -> "网络已断开，请检查网络后重试"
            is JsonParseException -> "数据解析失败，请联系管理员"
            is JSONException -> "数据解析失败，请联系管理员"
            is ParseException -> "数据解析失败，请联系管理员"
            is HttpException -> "网络错误，请退出重试，或联系管理员"
            else -> t.message ?: "未知错误,请完全退出应用稍后重试，如果仍有问题，请联系管理员"
        }
        toast(errorMessage)
    }
}