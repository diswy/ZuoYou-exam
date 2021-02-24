package com.zuoyouxue.ui.user


import android.text.TextUtils
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.diswy.foundation.base.fragment.BaseBindFragment
import com.diswy.foundation.view.dialog.FancyDialogFragment
import com.diswy.foundation.web.Resource
import com.diswy.foundation.web.Status
import com.ebd.common.App
import com.ebd.common.viewmodel.CommonViewModel
import com.ebd.common.vo.FindUser
import com.zuoyouxue.R
import com.zuoyouxue.databinding.FindAccountDialogBinding
import com.zuoyouxue.databinding.FragmentFindUserBinding


class FindUserFragment : BaseBindFragment<FragmentFindUserBinding>() {

    private val cvm: CommonViewModel by viewModels { App.instance.factory }

    private val observer = Observer<Resource<FindUser>> {
        when (it.status) {
            Status.LOADING -> {
                binding.tvFindInfo.visibility = View.GONE
            }
            Status.SUCCESS -> {
                showDialog("你的账号：${it.data!!.LoginName}")
            }
            Status.ERROR -> {
                binding.tvFindInfo.text = it.throwable?.message ?: "未知错误"
                binding.tvFindInfo.visibility = View.VISIBLE
            }
        }
    }

    override fun getLayoutRes(): Int = R.layout.fragment_find_user

    override fun initialize() {
        cvm.findUser.observe(this, observer)
    }

    override fun bindListener() {
        binding.btnClose.setOnClickListener {
            Navigation.findNavController(it).popBackStack()
        }
        binding.btnFind.setOnClickListener {
            findUser()
        }
    }

    private fun findUser() {
        val idCard = binding.etIdentity.text.toString().trim()
        if (TextUtils.isEmpty(idCard)) {
            binding.tvFindInfo.text = "身份证号码不能为空哦！请您重新输入"
            binding.tvFindInfo.visibility = View.VISIBLE
            return
        }
        cvm.findUser(idCard)
    }

    private fun showDialog(content: String) {
        FancyDialogFragment.create()
            .setLayoutRes(R.layout.find_account_dialog)
            .setWidth(requireActivity(), 280)
            .setViewListener { dialog, binding ->
                val bind: FindAccountDialogBinding = binding as FindAccountDialogBinding
                bind.tvUserAccount.text = content
                bind.dialogClose.setOnClickListener {
                    dialog.dismiss()
                }
                bind.btnFindAccount.setOnClickListener {
                    dialog.dismiss()
                }
            }
            .show(requireActivity().supportFragmentManager, "findSuccess")
    }

}
