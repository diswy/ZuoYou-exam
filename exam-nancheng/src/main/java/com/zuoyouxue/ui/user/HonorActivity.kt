package com.zuoyouxue.ui.user

import android.view.LayoutInflater
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.diswy.foundation.base.activity.BaseBindActivity
import com.diswy.foundation.base.adapter.BaseBindAdapter
import com.diswy.foundation.web.Resource
import com.diswy.foundation.web.Status
import com.ebd.common.App
import com.ebd.common.cache.loginId
import com.ebd.common.viewmodel.CommonViewModel
import com.ebd.common.vo.Honor
import com.zuoyouxue.R
import com.zuoyouxue.databinding.ActivityHonorBinding
import com.zuoyouxue.databinding.ItemHonorBinding

class HonorActivity : BaseBindActivity<ActivityHonorBinding>() {

    private val cvm: CommonViewModel by viewModels { App.instance.factory }

    private val adapter = object : BaseBindAdapter<Honor>(R.layout.item_honor) {
        override fun convert(helper: BaseViewHolder, item: Honor) {
            helper.getBinding<ItemHonorBinding>()?.apply {
                honor = item
                sort = helper.layoutPosition.toString()
                executePendingBindings()
            }
        }
    }

    private val observer: Observer<Resource<List<Honor>>> = Observer {
        when (it.status) {
            Status.LOADING -> {
            }
            Status.SUCCESS -> {
                showMe(it.data!!)
                adapter.setNewData(it.data as MutableList)
            }
            Status.ERROR -> {
                handleExceptions(it.throwable)
            }
        }
    }

    override fun statusDarkMode(): Boolean = true

    override fun getLayoutRes(): Int = R.layout.activity_honor

    override fun initialize() {
        binding.honorRecycler.addItemDecoration(myDivider(R.color.line))
        binding.honorRecycler.adapter = adapter

        cvm.honorList.observe(this, observer)
        cvm.getHonor()
    }

    override fun bindListener() {
        binding.honorIvBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun showMe(list: List<Honor>) {
        val data = list.find { item -> item.StudentId == loginId } ?: return
        val sort = list.indexOf(data) + 1
        val v = LayoutInflater.from(this).inflate(R.layout.head_honor_myself, null)
        val sortTv: TextView = v.findViewById(R.id.tv_sort)
        val nameTv: TextView = v.findViewById(R.id.tv_name)
        val flowerTv: TextView = v.findViewById(R.id.tv_safflower)
        val medalTv: TextView = v.findViewById(R.id.tv_honorary)
        sortTv.text = sort.toString()
        nameTv.text = data.Name
        flowerTv.text = data.FlowerCount.toString()
        medalTv.text = data.MedaCount.toString()
        adapter.addHeaderView(v)
    }

}
