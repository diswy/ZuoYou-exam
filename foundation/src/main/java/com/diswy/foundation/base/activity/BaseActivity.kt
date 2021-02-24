package com.diswy.foundation.base.activity

import android.Manifest
import android.content.ComponentCallbacks
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.diswy.foundation.base.Permission
import com.diswy.foundation.base.timer.ITimer
import com.diswy.foundation.quick.dip
import com.diswy.foundation.quick.toast
import com.diswy.foundation.view.drawable.DrawableBuilder
import com.ebd.foundation.R
import com.google.gson.JsonParseException
import com.umeng.analytics.MobclickAgent
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONException
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import retrofit2.HttpException
import timber.log.Timber
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.text.ParseException
import java.util.concurrent.CancellationException
import java.util.concurrent.TimeUnit

abstract class BaseActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    protected open val disposable = CompositeDisposable()
    /**
     * 保持屏幕常亮
     */
    protected open fun keepScreenOn() = false

    /**
     * 视图无默认颜色，可以减少视图层级。
     * 在主题中设置默认颜色，通常不必使用该方法
     */
    protected open fun emptyBackground() = false

    /**
     * 标题栏颜色模式
     */
    protected open fun statusDarkMode() = false

    /**
     * 全屏模式，该模式下看不到时间、电池等图标。沉浸式体验
     */
    protected open fun fullScreenMode() = false

    /**
     * 透明状态栏样式
     */
    protected open fun translucentMode() = false

    /**
     * 使用自定义像素密度，适配用
     */
    protected open fun customDensity() = false

    /**
     * 获取视图ID
     */
    abstract fun getLayoutRes(): Int

    protected open fun setView() {
        setContentView(getLayoutRes())
    }

    abstract fun initialize()

    protected open fun bindListener() {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        if (customDensity()) {
//            setCustomDensity()
//        }
        if (emptyBackground()) {
            window.decorView.background = null
        }
        if (keepScreenOn()) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
        if (statusDarkMode()) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        if (fullScreenMode()) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        } else if (translucentMode()) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            window.statusBarColor = Color.TRANSPARENT
        }
        ARouter.getInstance().inject(this)
        setView()
        initialize()
        bindListener()
    }

    override fun onResume() {
        super.onResume()
        MobclickAgent.onResume(this)
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPause(this)
    }

    override fun onDestroy() {
        disposable.clear()
        super.onDestroy()
    }

    /**
     * @return 状态栏高度
     */
    protected open fun getStatusBarHeight(): Int {
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        return resources.getDimensionPixelSize(resourceId)
    }

    /**
     * 使用主题颜色作为状态栏颜色
     */
    protected open fun initStatusBarColor() {
        val defaultColor = 0x00000000
        val attrsArray = intArrayOf(android.R.attr.colorPrimary)
        val typedArray = obtainStyledAttributes(attrsArray)
        val accentColor = typedArray.getColor(0, defaultColor)
        window.statusBarColor = accentColor
        typedArray.recycle()
    }

    /****************权限管理****************/
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {}

    protected fun hasCameraPermission(): Boolean {
        if (!EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA)) {
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.request_camera_permission),
                Permission.RC_CAMERA_PERM,
                Manifest.permission.CAMERA
            )
        }
        return EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA)
    }

    protected fun hasStoragePermission(): Boolean {
        if (!EasyPermissions.hasPermissions(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        ) {
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.request_storage_permission),
                Permission.RC_STORAGE_PERM,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }
        return EasyPermissions.hasPermissions(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }

    protected fun hasRecordPermission(): Boolean {
        if (!EasyPermissions.hasPermissions(this, Manifest.permission.RECORD_AUDIO)) {
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.request_record_permission),
                Permission.RC_RECORD_PERM,
                Manifest.permission.RECORD_AUDIO
            )
        }
        return EasyPermissions.hasPermissions(this, Manifest.permission.RECORD_AUDIO)
    }

    /****************权限管理****************/

    protected open fun handleExceptions(t: Throwable?) {
        if (t == null) {
            toast("未知错误")
            return
        }
        if (t is CancellationException) {
            return
        }
        Timber.e(t)
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

    /**
     * 计时任务,如果只关心过程或结果可使用子类接口
     * 注意：非唯一任务，不要多次启动
     * @param second 秒
     * @param timer 回调
     */
    protected open fun timer(second: Long, timer: ITimer) {
        val timeTask = Flowable.intervalRange(0, second, 0, 1, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                timer.onTime((second - it - 1).toInt())
            }
            .doOnComplete {
                timer.onTimeEnd()
            }
            .doOnError {
            }
            .subscribe()
        disposable.add(timeTask)
    }

    /**
     * @param colorId 颜色ID
     * @return 自定义颜色的分割线
     */
    protected open fun myDivider(@ColorRes colorId: Int): DividerItemDecoration {
        // 分割线初始化
        val divider = DividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        val mDrawable = DrawableBuilder()
            .solidColor(ContextCompat.getColor(this, colorId))
            .height(dip(1))
            .build()
        divider.setDrawable(mDrawable)
        return divider
    }

    /**
     * 自定义density
     */
    private var sNoncompatDensity: Float = 0f
    private var sNoncompatScaledDensity: Float = 0f

    protected open fun setCustomDensity() {
        val appDisplayMetrics = application.resources.displayMetrics
        if (sNoncompatDensity == 0f) {
            sNoncompatDensity = appDisplayMetrics.density
            sNoncompatScaledDensity = appDisplayMetrics.scaledDensity
            application.registerComponentCallbacks(object : ComponentCallbacks {
                override fun onLowMemory() {
                }

                override fun onConfigurationChanged(newConfig: Configuration?) {
                    if (newConfig != null && newConfig.fontScale > 0) {
                        sNoncompatScaledDensity = application.resources.displayMetrics.scaledDensity
                    }
                }
            })
        }
        val targetDensity: Float = appDisplayMetrics.widthPixels / 1280f
        val targetScaledDensity = targetDensity * (sNoncompatScaledDensity / sNoncompatDensity)
        val targetDensityDpi: Int = (160 * targetDensity).toInt()
        appDisplayMetrics.density = targetDensity
        appDisplayMetrics.scaledDensity = targetScaledDensity
        appDisplayMetrics.densityDpi = targetDensityDpi

        val activityDisplayMetrics = resources.displayMetrics
        activityDisplayMetrics.density = targetDensity
        activityDisplayMetrics.scaledDensity = targetScaledDensity
        activityDisplayMetrics.densityDpi = targetDensityDpi
    }
}