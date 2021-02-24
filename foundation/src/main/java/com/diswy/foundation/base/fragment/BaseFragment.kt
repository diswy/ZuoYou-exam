package com.diswy.foundation.base.fragment

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
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

abstract class BaseFragment : Fragment(), EasyPermissions.PermissionCallbacks {

    protected open val disposable = CompositeDisposable()

    private lateinit var rootView: View

    abstract fun getLayoutRes(): Int

    abstract fun initialize()

    protected open fun bindListener() {}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (!::rootView.isInitialized) {
            rootView = inflater.inflate(getLayoutRes(), container, false)
        } else {
            val parent = rootView.parent as ViewGroup
            parent.removeView(rootView)
        }
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        ARouter.getInstance().inject(this)
        initialize()
        bindListener()
    }

    override fun onDestroyView() {
        disposable.clear()
        super.onDestroyView()
    }

    /**
     * 强制关闭软键盘
     * @param view 这里可以是任意view
     */
    protected open fun hideSoftKeyboard(context: Context, view: View) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
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
        if (!EasyPermissions.hasPermissions(requireContext(), Manifest.permission.CAMERA)) {
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.request_camera_permission),
                Permission.RC_CAMERA_PERM,
                Manifest.permission.CAMERA
            )
        }
        return EasyPermissions.hasPermissions(requireContext(), Manifest.permission.CAMERA)
    }

    protected fun hasStoragePermission(): Boolean {
        if (!EasyPermissions.hasPermissions(
                requireContext(),
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
            requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }

    protected fun hasRecordPermission(): Boolean {
        if (!EasyPermissions.hasPermissions(requireContext(), Manifest.permission.RECORD_AUDIO)) {
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.request_record_permission),
                Permission.RC_RECORD_PERM,
                Manifest.permission.RECORD_AUDIO
            )
        }
        return EasyPermissions.hasPermissions(requireContext(), Manifest.permission.RECORD_AUDIO)
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
        if (second == 0L) {
            return
        }
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

    protected open fun ctx(block: (ctx: Context) -> Unit) {
        activity?.let(block)
    }

    /**
     * @param colorId 颜色ID
     * @return 自定义颜色的分割线
     */
    protected open fun myDivider(@ColorRes colorId: Int): DividerItemDecoration {
        // 分割线初始化
        val divider = DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)
        val mDrawable = DrawableBuilder()
            .solidColor(ContextCompat.getColor(requireContext(), colorId))
            .height(requireContext().dip(1))
            .build()
        divider.setDrawable(mDrawable)
        return divider
    }
}