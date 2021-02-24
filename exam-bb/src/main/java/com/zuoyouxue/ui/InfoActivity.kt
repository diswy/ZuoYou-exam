package com.zuoyouxue.ui

import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.diswy.foundation.base.activity.BaseBindActivity
import com.diswy.foundation.quick.toast
import com.diswy.foundation.web.Resource
import com.diswy.foundation.web.Status
import com.ebd.common.App
import com.ebd.common.Page
import com.ebd.common.cache.getUser
import com.ebd.common.viewmodel.CoreViewModel
import com.ebd.common.viewmodel.WorkViewModel
import com.ebd.common.vo.Homework
import com.zuoyouxue.R
import com.zuoyouxue.databinding.ActivityInfoBinding

@Route(path = Page.INFO)
class InfoActivity : BaseBindActivity<ActivityInfoBinding>() {

    private var homework: Homework? = null
    private val workViewModel: WorkViewModel by viewModels { App.instance.factory }
    private val coreViewModel: CoreViewModel by viewModels { App.instance.factory }

    private val observer = Observer<Resource<List<Homework>>> {
        when (it.status) {
            Status.LOADING -> {
            }
            Status.SUCCESS -> {
                if (!it.data.isNullOrEmpty()) {
                    homework = it.data!![0]
                }
            }
            Status.ERROR -> handleExceptions(it.throwable)
        }
    }

    override fun fullScreenMode(): Boolean = true

    override fun getLayoutRes(): Int = R.layout.activity_info

    override fun initialize() {
        getUser()?.let {
            binding.userName.text = "亲爱的%s同学：".format(it.Name)
        }
        workViewModel.myHomework.observe(this, observer)
        workViewModel.getHomeWork(1)
    }

    override fun bindListener() {
        binding.btnStart.setOnClickListener {
            if (homework != null) {
                coreViewModel.startAnswerWork(homework, this)
            } else {
                toast("关卡准备中...")
            }
        }
    }


}