package com.zuoyouxue.ui.user


import android.text.TextUtils
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.diswy.foundation.base.fragment.BaseBindFragment
import com.diswy.foundation.base.timer.ITimer
import com.diswy.foundation.quick.toast
import com.diswy.foundation.web.Resource
import com.diswy.foundation.web.Status
import com.ebd.common.App
import com.ebd.common.config.EbdState
import com.ebd.common.viewmodel.CommonViewModel
import com.ebd.common.vo.BaseResponse
import com.zuoyouxue.R
import com.zuoyouxue.databinding.FragmentResetPwdBinding


class ResetPwdFragment : BaseBindFragment<FragmentResetPwdBinding>(), ITimer {

    override fun onTime(second: Int) {
        binding.btnIdentifyingCode.isEnabled = false
        val format = "%d s"
        binding.btnIdentifyingCode.text = format.format(second)
    }

    override fun onTimeEnd() {
        binding.btnIdentifyingCode.text = "获取验证码"
        binding.btnIdentifyingCode.isEnabled = true
    }

    private val cvm: CommonViewModel by viewModels { App.instance.factory }

    private val codeObserver = Observer<Resource<BaseResponse<Unit>>> {
        when (it.status) {
            Status.LOADING -> {
            }
            Status.SUCCESS -> {
                if (it.data!!.isSuccess) {
                    activity?.toast(it.data!!.message)
                    (activity as LoginActivity).tickTok(60)
                } else {
                    activity?.toast(it.data?.message ?: "未知错误，请退出或稍后重新尝试")
                    binding.btnIdentifyingCode.isEnabled = true
                }
            }
            Status.ERROR -> {
                handleExceptions(it.throwable)
                binding.btnIdentifyingCode.text = "获取验证码"
                binding.btnIdentifyingCode.isEnabled = true
            }
        }
    }

    private val passwordObserver = Observer<Resource<BaseResponse<Unit>>> {
        when (it.status) {
            Status.LOADING -> {
            }
            Status.SUCCESS -> {
                activity?.toast(it.data?.message ?: "未知错误，请退出或稍后重新尝试")
                if (it.data?.isSuccess == true) {
                    Navigation.findNavController(binding.btnClose).popBackStack()
                }
            }
            Status.ERROR -> {
                binding.resetPwdErrorInfo.text = it.throwable?.message ?: "未知错误"
                binding.resetPwdErrorInfo.visibility = View.VISIBLE
            }
        }
    }

    override fun getLayoutRes(): Int = R.layout.fragment_reset_pwd

    override fun initialize() {
        (activity as LoginActivity).setCurrentTimer(this)
        cvm.phoneCode.observe(this, codeObserver)
        cvm.newPwd.observe(this, passwordObserver)
    }

    override fun bindListener() {
        binding.btnClose.setOnClickListener {
            Navigation.findNavController(it).popBackStack()
        }

        binding.btnIdentifyingCode.setOnClickListener {
            val loginName = binding.etLoginName.text.toString().trim()
            if (TextUtils.isEmpty(loginName)) {
                binding.resetPwdErrorInfo.visibility = View.VISIBLE
                binding.resetPwdErrorInfo.text = "用户名不能为空，请重新输入"
            } else {
                binding.resetPwdErrorInfo.visibility = View.GONE
                cvm.getPhoneCode(loginName, EbdState.FIND_PWD)
            }
        }

        binding.btnFindPwdSubmit.setOnClickListener {
            val userName = binding.etLoginName.text.toString().trim()
            if (checkEdit() && !TextUtils.isEmpty(userName)) {
                val pwd = binding.etNewPwd.text.toString().trim()
                val code = binding.etIdentifyingCode.text.toString().trim()
                cvm.updatePwd(userName, pwd, code)
            }
        }
    }

    //验证输入框
    private fun checkEdit(): Boolean {
        if (TextUtils.isEmpty(binding.etIdentifyingCode.text.toString())) {
            binding.resetPwdErrorInfo.text = "验证码不能为空，请重新输入"
            binding.resetPwdErrorInfo.visibility = View.VISIBLE
            return false
        } else {
            binding.resetPwdErrorInfo.visibility = View.GONE
            if (TextUtils.isEmpty(binding.etNewPwd.text.toString()) || binding.etNewPwd.text.length < 6) {
                binding.resetPwdErrorInfo.text = "新密码不能为空且必须是6位数以上，请重新输入新密码"
                binding.resetPwdErrorInfo.visibility = View.VISIBLE
                return false
            } else {
                binding.resetPwdErrorInfo.visibility = View.GONE
                if (TextUtils.isEmpty(binding.etRepeatNewPwd.text.toString())) {
                    binding.resetPwdErrorInfo.text = "确认新密码不能为空，请重新输入"
                    binding.resetPwdErrorInfo.visibility = View.VISIBLE
                    return false
                } else {
                    binding.resetPwdErrorInfo.visibility = View.GONE
                    if (binding.etNewPwd.text.toString() != binding.etRepeatNewPwd.text.toString()) {
                        binding.resetPwdErrorInfo.text = "新密码两次输入不一致，请重新输入"
                        binding.resetPwdErrorInfo.visibility = View.VISIBLE
                        return false
                    }
                }
            }
        }
        binding.resetPwdErrorInfo.visibility = View.GONE
        return true
    }

}
