package com.zuoyouxue.ui.user


import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.text.TextUtils
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.launcher.ARouter
import com.diswy.foundation.base.Permission
import com.diswy.foundation.base.fragment.BaseBindFragment
import com.diswy.foundation.base.timer.ITimer
import com.diswy.foundation.quick.loadCircle
import com.diswy.foundation.quick.toast
import com.diswy.foundation.tools.CleanCacheTool
import com.diswy.foundation.view.FancyButton
import com.diswy.foundation.view.dialog.FancyDialogFragment
import com.diswy.foundation.web.Resource
import com.diswy.foundation.web.Status
import com.ebd.common.App
import com.ebd.common.Page
import com.ebd.common.cache.CacheTool
import com.ebd.common.cache.getUser
import com.ebd.common.cache.loginId
import com.ebd.common.cache.updateUserPhone
import com.ebd.common.config.CacheKey
import com.ebd.common.config.Ebd
import com.ebd.common.config.EbdState
import com.ebd.common.viewmodel.CommonViewModel
import com.ebd.common.vo.BaseResponse
import com.just.agentweb.AgentWebConfig
import com.yalantis.ucrop.UCrop
import com.zuoyouxue.R
import com.zuoyouxue.databinding.DialogBindPhoneBinding
import com.zuoyouxue.databinding.FragmentMineBinding
import com.zuoyouxue.ui.EbdWebActivity
import kit.diswy.photo.singleImagePicker
import pub.devrel.easypermissions.AfterPermissionGranted
import java.io.File


/**
 * 个人中心
 */
class MineFragment : BaseBindFragment<FragmentMineBinding>() {

    private var sendButton: FancyButton? = null
    private var bindStatus: Int = 0// 0绑定 1修改
    private var tel: String = ""

    private val codeObserver = Observer<Resource<BaseResponse<Unit>>> {
        when (it.status) {
            Status.LOADING -> {
            }
            Status.SUCCESS -> {
                if (it.data!!.isSuccess) {
                    toast(it.data!!.message)
                    timer(60, object : ITimer {
                        override fun onTime(second: Int) {
                            sendButton?.isEnabled = false
                            sendButton?.text = (second + 1).toString()
                        }

                        override fun onTimeEnd() {
                            sendButton?.isEnabled = true
                            sendButton?.text = "验证码"
                        }

                    })
                } else {
                    toast(it.data?.message ?: "验证码发送失败")
                    sendButton?.isEnabled = true
                }
            }
            Status.ERROR -> {
                handleExceptions(it.throwable)
                sendButton?.text = "获取验证码"
                sendButton?.isEnabled = true
            }
        }
    }

    private val bindObserver = Observer<Resource<BaseResponse<Unit>>> {
        when (it.status) {
            Status.LOADING -> {
            }
            Status.SUCCESS -> {
                toast(it.data!!.message)
                if (it.data!!.isSuccess) {
                    mBindDialog.dismiss()
                    updateUserPhone(tel)
                    updatePhone()
                }
            }
            Status.ERROR -> {
                handleExceptions(it.throwable)
            }
        }
    }

    private var dialogTitle: TextView? = null
    private lateinit var dialogPhone: EditText
    private lateinit var dialogCode: EditText
    private lateinit var dialogPassword: EditText
    private val mBindDialog by lazy {
        FancyDialogFragment.create()
            .setLayoutRes(R.layout.dialog_bind_phone)
            .setWidth(requireActivity(), 480)
            .setCanCancelOutside(false)
            .setViewListener { dialog, dialogBinding ->
                dialogBinding as DialogBindPhoneBinding

                dialogTitle = dialogBinding.tvTitle
                dialogPhone = dialogBinding.etPhone
                dialogCode = dialogBinding.etCode
                dialogPassword = dialogBinding.etPassword
                sendButton = dialogBinding.sendCode

                if (bindStatus == 1) {
                    dialogTitle?.text = "修改手机号"
                } else {
                    dialogTitle?.text = "绑定手机号"
                }

                dialogBinding.sendCode.setOnClickListener {
                    val inputPhone = dialogBinding.etPhone.text.toString()
                    if (TextUtils.isEmpty(inputPhone)) {
                        toast("请检查手机号")
                        return@setOnClickListener
                    }
                    cvm.getPhoneCode(inputPhone, EbdState.BIND_USER)
                }
                dialogBinding.btnClose.setOnClickListener {
                    dialog.dismiss()
                }
                dialogBinding.bindConfirm.setOnClickListener {
                    val phone = dialogBinding.etPhone.text.toString()
                    val code = dialogBinding.etCode.text.toString()
                    val password = dialogBinding.etPassword.text.toString()
                    if (TextUtils.isEmpty(phone) || phone.length < 11) {
                        toast("请输入正确的手机号码")
                        return@setOnClickListener
                    }
                    if (TextUtils.isEmpty(code)) {
                        toast("请输入手机验证码")
                        return@setOnClickListener
                    }
                    if (TextUtils.isEmpty(password)) {
                        toast("请输入用户密码")
                        return@setOnClickListener
                    }
                    hideSoftKeyboard(requireContext(), it)
                    tel = phone
                    cvm.bindPhone(bindStatus, phone, code, password)
                }
            }
            .setDismissListener {
                dialogPhone.setText("")
                dialogCode.setText("")
                dialogPassword.setText("")
            }
    }

    private val cvm: CommonViewModel by viewModels { App.instance.factory }

    private val observer: Observer<Resource<String>> = Observer {
        when (it.status) {
            Status.LOADING -> {
            }
            Status.SUCCESS -> {
                binding.userCivAvatar.loadCircle(requireActivity(), it.data!!)
                toast("头像更新成功")
            }
            Status.ERROR -> {
                handleExceptions(it.throwable)
            }
        }
    }

    override fun getLayoutRes(): Int = R.layout.fragment_mine

    override fun initialize() {
        binding.user = getUser()
        cvm.avatar.observe(this, observer)
        cvm.phoneCode.observe(this, codeObserver)
        cvm.bindPhone.observe(this, bindObserver)
        updatePhone()
    }

    override fun bindListener() {
        binding.userCivAvatar.setOnClickListener {
            startPicker()
        }
        binding.userFlower.setOnClickListener {
            startActivity(Intent(context, HonorDetailsActivity::class.java))
        }
        binding.userAward.setOnClickListener {
            startActivity(Intent(context, HonorDetailsActivity::class.java))
        }
        binding.btnWorkMessage.setOnClickListener {
            startActivity(Intent(context, MessageActivity::class.java))
        }
        binding.btnHonor.setOnClickListener {
            startActivity(Intent(context, HonorActivity::class.java))
        }
        binding.btnLeader.setOnClickListener {
            EbdWebActivity.start(requireActivity(), Ebd.URL_LEADER.format(loginId))
        }
        binding.btnFeedBack.setOnClickListener {
            startActivity(Intent(context, FeedBackActivity::class.java))
        }
        binding.btnModifyPassword.setOnClickListener {
            startActivity(Intent(context, ModifyPasswordActivity::class.java))
        }
        binding.btnClearCache.setOnClickListener {
            AlertDialog.Builder(context)
                .setMessage("当前缓存%s,是否清理？".format(CleanCacheTool.getTotalCacheSize(context)))
                .setPositiveButton("确定") { _, _ ->
                    AgentWebConfig.clearDiskCache(context)
                    CleanCacheTool.clearAllCache(context)
                }
                .setNegativeButton("我再想想", null)
                .show()
        }
        binding.btnAboutUs.setOnClickListener {
            startActivity(Intent(context, AboutUsActivity::class.java))
        }
        binding.btnLogout.setOnClickListener {
            AlertDialog.Builder(context).setMessage("你确定要退出登录么？")
                .setPositiveButton("确定") { _, _ ->
                    CacheTool.setValue(CacheKey.USER to "")
                    activity?.finish()
                    ARouter.getInstance().build(Page.LOGIN).navigation()
                }
                .setNegativeButton("我再想想", null)
                .show()
        }
        binding.userTvPhone.setOnClickListener {
            val text = binding.userTvPhone.text.toString()
            bindStatus = if (text.contains("Tel")) {
                dialogTitle?.text = "修改手机号"
                1
            } else {
                dialogTitle?.text = "绑定手机号"
                0
            }
            if (!mBindDialog.isVisible) {
                mBindDialog.show(parentFragmentManager, "bind_phone")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            data?.let {
                val file = File(UCrop.getOutput(data)?.path)
                cvm.updateAvatar(requireActivity(), file)
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            toast("发生错误，请重新拍照，或换张图片")
        }
    }

    @AfterPermissionGranted(Permission.RC_STORAGE_PERM)
    private fun startPicker() {
        if (hasStoragePermission()) {
            startPickerCamera()
        }
    }

    @AfterPermissionGranted(Permission.RC_CAMERA_PERM)
    private fun startPickerCamera() {
        if (hasCameraPermission()) {
            singleImagePicker(6)
        }
    }


    private fun updatePhone() {
        getUser()?.let {
            binding.userTvPhone.text =
                if (TextUtils.isEmpty(it.Phone)) "未绑定手机" else "Tel：${it.Phone}"
        }
    }
}
