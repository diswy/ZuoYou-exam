package com.zuoyouxue.ui.user

import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.diswy.foundation.base.activity.BaseBindActivity
import com.diswy.foundation.base.adapter.BaseBindAdapter
import com.diswy.foundation.web.Resource
import com.diswy.foundation.web.Status
import com.ebd.common.App
import com.ebd.common.viewmodel.CommonViewModel
import com.ebd.common.vo.HonorMe
import com.zuoyouxue.R
import com.zuoyouxue.databinding.ActivityHonorDetailsBinding
import com.zuoyouxue.databinding.ItemHonorMeBinding

class HonorDetailsActivity : BaseBindActivity<ActivityHonorDetailsBinding>() {

    private val cvm: CommonViewModel by viewModels { App.instance.factory }

    private val adapter = object : BaseBindAdapter<HonorMe>(R.layout.item_honor_me) {
        override fun convert(helper: BaseViewHolder, item: HonorMe) {
            helper.getBinding<ItemHonorMeBinding>()?.apply {
                myHonor = item
                executePendingBindings()
            }
        }
    }

    private val observer: Observer<Resource<List<HonorMe>>> = Observer {
        when (it.status) {
            Status.LOADING -> {
            }
            Status.SUCCESS -> {
                adapter.setNewData(it.data as MutableList)
            }
            Status.ERROR -> {
                handleExceptions(it.throwable)
            }
        }
    }

    override fun translucentMode(): Boolean = true

    override fun getLayoutRes(): Int = R.layout.activity_honor_details

    override fun initialize() {
        binding.honorMeRecycler.addItemDecoration(myDivider(R.color.line))
        binding.honorMeRecycler.adapter = adapter

        cvm.myHonor.observe(this, observer)
        cvm.getMyHonor()
    }

    override fun bindListener() {
        binding.safflowerIvBack.setOnClickListener {
            onBackPressed()
        }
    }
}
