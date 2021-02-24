package com.zuoyouxue.ui.user


import android.os.Environment
import android.text.InputType
import android.text.TextUtils
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.alibaba.android.arouter.launcher.ARouter
import com.diswy.foundation.base.fragment.BaseBindFragment
import com.diswy.foundation.quick.toast
import com.diswy.foundation.tools.ACache
import com.diswy.foundation.web.Resource
import com.diswy.foundation.web.Status
import com.ebd.common.App
import com.ebd.common.Page
import com.ebd.common.cache.CacheTool
import com.ebd.common.cache.saveUser
import com.ebd.common.config.CacheKey
import com.ebd.common.viewmodel.CommonViewModel
import com.ebd.common.viewmodel.ToolViewModel
import com.ebd.common.vo.User
import com.google.gson.Gson
import com.zuoyouxue.R
import com.zuoyouxue.databinding.FragmentLoginUserBinding
import com.zuoyouxue.ui.EbdWebActivity
import java.io.File
import java.io.FileOutputStream


class LoginUserFragment : BaseBindFragment<FragmentLoginUserBinding>() {

    private val toolVm: ToolViewModel by viewModels { App.instance.factory }
    private val mCache by lazy { ACache.get(App.instance) }
    private var account = ""
    private var password = ""

    private val cvm: CommonViewModel by viewModels { App.instance.factory }

    private val observer = Observer<Resource<User>> {
        when (it.status) {
            Status.LOADING -> {
            }
            Status.SUCCESS -> {
                sharedUser(Gson().toJson(it.data))

                saveUser(it.data!!, account, password)
                // 临时保存1个小时的账号密码
                mCache.put(CacheKey.TMP_USER, account, ACache.TIME_HOUR / 2)
                mCache.put(CacheKey.TMP_PWD, password, ACache.TIME_HOUR / 2)
                toast("登录成功")
                ARouter.getInstance().build(Page.INFO).navigation()
                activity?.finish()
            }
            Status.ERROR -> {
                handleExceptions(it.throwable)
            }
        }
    }

    override fun getLayoutRes(): Int = R.layout.fragment_login_user

    override fun initialize() {
        val mCache = ACache.get(activity)
        val account: String? = mCache.getAsString(CacheKey.TMP_USER)
        if (account != null) {
            binding.etAccount.setText(CacheTool.getValue(CacheKey.ACCOUNT, ""))
            binding.etPassword.setText(CacheTool.getValue(CacheKey.PASSWORD, ""))
        }
        cvm.user.observe(this, observer)
        binding.textVer.text = toolVm.getAppInfo().versionName()

        cvm.dyUrl.observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    it.data?.let { url ->
                        EbdWebActivity.start(requireContext(), url)
                    }
                }
                Status.LOADING -> {
                }
                Status.ERROR -> {
                    handleExceptions(it.throwable)
                }
            }
        })
    }

    override fun bindListener() {
        binding.btnLogin.setOnClickListener {
            login()
        }
        binding.btnFindAccount.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_to_find)
        }
        binding.btnResetPwd.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_to_reset)
        }
        //点击图标显示或隐藏密码
        binding.showPassword.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.etPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                binding.etPassword.inputType =
                    InputType.TYPE_TEXT_VARIATION_PASSWORD or InputType.TYPE_CLASS_TEXT
            }
        }

        binding.btnTest.setOnClickListener {
            cvm.getHeartQuestion()
        }
    }

    private fun login() {
        account = binding.etAccount.text.toString().trim()
        password = binding.etPassword.text.toString().trim()
        if (TextUtils.isEmpty(account)) {
            toast("请输入账号")
            return
        }
        if (TextUtils.isEmpty(password)) {
            toast("请输入密码")
            return
        }
        cvm.login(account, password)
    }


    private fun sharedUser(data: String) {
        val path = Environment.getExternalStorageDirectory().absolutePath.plus("/yunketang/shared")
        val file = File(path)
        if (!file.exists()) {
            file.mkdirs()
        }

        val mFile = File(path, "user")
        val outStream = FileOutputStream(mFile)
        outStream.write(data.toByteArray())
        outStream.close()
    }
}
