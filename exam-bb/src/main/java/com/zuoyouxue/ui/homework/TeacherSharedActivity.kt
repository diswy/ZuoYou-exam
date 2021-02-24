package com.zuoyouxue.ui.homework

import android.view.LayoutInflater
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.diswy.foundation.base.PageInfo
import com.diswy.foundation.base.activity.BaseToolbarBindActivity
import com.diswy.foundation.web.Resource
import com.diswy.foundation.web.Status
import com.ebd.common.App
import com.ebd.common.cache.getSubject
import com.ebd.common.viewmodel.WorkViewModel
import com.ebd.common.vo.SharedWork
import com.ebd.common.vo.User
import com.google.android.material.tabs.TabLayout
import com.zuoyouxue.R
import com.zuoyouxue.adapter.SharedAdapter
import com.zuoyouxue.databinding.ActivityTeacherSharedBinding

class TeacherSharedActivity : BaseToolbarBindActivity<ActivityTeacherSharedBinding>(),
    TabLayout.OnTabSelectedListener {

    private val adapter = SharedAdapter()
    private val pageInfo: PageInfo<SharedWork.SharedWorkItem> = PageInfo(adapter)

    private val subjectList = ArrayList<User.Subject>()

    private val workViewModel: WorkViewModel by viewModels { App.instance.factory }

    private val observer: Observer<Resource<SharedWork>> = Observer {
        when (it.status) {
            Status.LOADING -> {
            }
            Status.SUCCESS -> {
                binding.recycler.refresh.finishRefresh()
                pageInfo.loadData(it.data!!.DataList)
            }
            Status.ERROR -> {
                handleExceptions(it.throwable)
            }
        }
    }

    override fun pageTitle(): String = "老师分享"

    override fun getLayoutRes(): Int = R.layout.activity_teacher_shared

    override fun initialize() {
        binding.recycler.rv.adapter = adapter

        subjectList.add(User.Subject(null, "全部", 0))
        subjectList.addAll(getSubject())
        subjectList.forEach { subject ->
            val v = LayoutInflater.from(this).inflate(R.layout.item_tab_work, null)
            val tv: TextView = v.findViewById(R.id.tv_tab_item)
            tv.text = subject.Name
            binding.sharedTab.addTab(binding.sharedTab.newTab().setCustomView(v))
        }

        workViewModel.sharedList.observe(this, observer)
        workViewModel.getTeacherShared(pageInfo.getPageIndex())
    }

    override fun bindListener() {
        binding.sharedTab.addOnTabSelectedListener(this)

        adapter.loadMoreModule?.setOnLoadMoreListener {
            workViewModel.getTeacherShared(pageInfo.getPageIndex())
        }

        binding.recycler.refresh.setOnRefreshListener {
            pageInfo.reloadData()
            workViewModel.getTeacherShared(pageInfo.getPageIndex())
        }
    }

    override fun onTabReselected(mTab: TabLayout.Tab?) {

    }

    override fun onTabUnselected(mTab: TabLayout.Tab?) {

    }

    override fun onTabSelected(mTab: TabLayout.Tab) {
        pageInfo.reloadData()
        workViewModel.subjectId = subjectList[mTab.position].Id
        workViewModel.getTeacherShared(pageInfo.getPageIndex())
    }

}
