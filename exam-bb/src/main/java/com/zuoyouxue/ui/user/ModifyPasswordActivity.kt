package com.zuoyouxue.ui.user

import android.text.TextUtils
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.diswy.foundation.base.activity.BaseToolbarBindActivity
import com.diswy.foundation.quick.toast
import com.diswy.foundation.web.Resource
import com.diswy.foundation.web.Status
import com.ebd.common.App
import com.ebd.common.viewmodel.CommonViewModel
import com.ebd.common.vo.BaseResponse
import com.zuoyouxue.R
import com.zuoyouxue.databinding.ActivityModifyPasswordBinding

class ModifyPasswordActivity : BaseToolbarBindActivity<ActivityModifyPasswordBinding>() {

    private val cvm: CommonViewModel by viewModels { App.instance.factory }

    private val passwordObserver = Observer<Resource<BaseResponse<Unit>>> {
        when (it.status) {
            Status.LOADING -> {
            }
            Status.SUCCESS -> {
                if (it.data!!.isSuccess) {
                    toast(it.data?.message ?: "")
                    finish()
                } else {
                    binding.editErrorInfo.text = it.data?.message ?: "未知错误"
                    binding.editErrorInfo.visibility = View.VISIBLE
                }
            }
            Status.ERROR -> {
                binding.editErrorInfo.text = it.throwable?.message ?: "未知错误"
                binding.editErrorInfo.visibility = View.VISIBLE
            }
        }
    }

    override fun pageTitle(): String = "修改密码"

    override fun getLayoutRes(): Int = R.layout.activity_modify_password

    override fun initialize() {
        cvm.newPwd.observe(this, passwordObserver)

        binding.btnEditPwdSubmit.setOnClickListener {
            if (checkEdit()) {
                cvm.modifyPwd(binding.oldPwd.text.toString(), binding.newPwd.text.toString())
            }
        }
    }

    private fun checkEdit(): Boolean {
        if (TextUtils.isEmpty(binding.oldPwd.text.toString())) {
            binding.editErrorInfo.visibility = View.VISIBLE
            binding.editErrorInfo.text = "旧密码不能为空，请重新输入"
            return false
        } else {
            binding.editErrorInfo.visibility = View.GONE
            if (TextUtils.isEmpty(binding.newPwd.text.toString()) || binding.newPwd.text.length < 6) {
                binding.editErrorInfo.visibility = View.VISIBLE
                binding.editErrorInfo.text = "新密码不能为空且必须是6位数以上，请重新输入新密码"
                return false
            } else {
                binding.editErrorInfo.visibility = View.GONE
                if (TextUtils.isEmpty(binding.repeatNewPwd.text.toString())) {
                    binding.editErrorInfo.visibility = View.VISIBLE
                    binding.editErrorInfo.text = "确认新密码不能为空，请重新输入"
                    return false
                } else {
                    binding.editErrorInfo.visibility = View.GONE
                    if (binding.newPwd.text.toString() != binding.repeatNewPwd.text.toString()) {
                        binding.editErrorInfo.visibility = View.VISIBLE
                        binding.editErrorInfo.text = "新密码两次输入不一致，请重新输入"
                        return false
                    }
                }
            }
        }
        return true
    }

}
