package com.zuoyouxue.ui.homework

import android.view.LayoutInflater
import android.widget.TextView
import com.diswy.foundation.base.activity.BaseToolbarBindActivity
import com.ebd.common.cache.getSubject
import com.ebd.common.cache.loginId
import com.ebd.common.config.Ebd
import com.ebd.common.vo.User
import com.google.android.material.tabs.TabLayout
import com.zuoyouxue.R
import com.zuoyouxue.databinding.ActivityCollectionBinding

class CollectionActivity : BaseToolbarBindActivity<ActivityCollectionBinding>(),
    TabLayout.OnTabSelectedListener {

    private val subjectList = ArrayList<User.Subject>()

    override fun pageTitle(): String = "我的收藏"

    override fun getLayoutRes(): Int = R.layout.activity_collection

    override fun initialize() {
        lifecycle.addObserver(binding.collectWeb)
        binding.collectWeb.load(this, Ebd.URL_COLLECT.format(loginId))

        subjectList.add(User.Subject(null, "全部", 0))
        subjectList.addAll(getSubject())
        subjectList.forEach { subject ->
            val v = LayoutInflater.from(this).inflate(R.layout.item_tab_work, null)
            val tv: TextView = v.findViewById(R.id.tv_tab_item)
            tv.text = subject.Name
            binding.collectTab.addTab(binding.collectTab.newTab().setCustomView(v))
        }
    }

    override fun bindListener() {
        binding.collectTab.addOnTabSelectedListener(this)
    }

    override fun onTabReselected(mTab: TabLayout.Tab?) {
    }

    override fun onTabUnselected(mTab: TabLayout.Tab?) {
    }

    override fun onTabSelected(mTab: TabLayout.Tab) {
        if (mTab.position == 0) {
            binding.collectWeb
                .load(this, Ebd.URL_COLLECT.format(loginId))
        } else {
            binding.collectWeb
                .load(this, Ebd.URL_COLLECT_ID.format(loginId, subjectList[mTab.position].Id))
        }
    }
}
