package com.zuoyouxue.ui.homework

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.diswy.foundation.base.PageInfo
import com.diswy.foundation.base.activity.BaseBindActivity
import com.diswy.foundation.view.drawable.spec.ControlDrawableSpec
import com.diswy.foundation.web.Resource
import com.diswy.foundation.web.Status
import com.ebd.common.App
import com.ebd.common.cache.getSubject
import com.ebd.common.config.EbdState
import com.ebd.common.viewmodel.WorkViewModel
import com.ebd.common.vo.Homework
import com.ebd.common.vo.User
import com.google.android.material.tabs.TabLayout
import com.zuoyouxue.R
import com.zuoyouxue.adapter.HomeworkAdapter
import com.zuoyouxue.databinding.ActivityMyWorkBinding
import kotlinx.android.synthetic.main.activity_my_work.*

/**
 * 我的作业页，核心业务
 */
class MyWorkActivity : BaseBindActivity<ActivityMyWorkBinding>(),
    TabLayout.OnTabSelectedListener {

    private val adapter = HomeworkAdapter()
    private val pageInfo: PageInfo<Homework> = PageInfo(adapter)

    private val workViewModel: WorkViewModel by viewModels { App.instance.factory }
    private val subjectList = ArrayList<User.Subject>()// 学科

    private val observer = Observer<Resource<List<Homework>>> {
        when (it.status) {
            Status.LOADING -> {
                if (pageInfo.getPageIndex() == 1) {// 首屏加载骨架屏
                    adapter.showSkeleton(binding.recycler.rv, R.layout.skeleton_item_work)
                }
            }
            Status.SUCCESS -> {
                if (pageInfo.getPageIndex() == 1) {// 首屏加载骨架屏
                    adapter.hideSkeleton()
                }
                binding.recycler.refresh.finishRefresh()
                pageInfo.loadData(it.data)
            }
            Status.ERROR -> handleExceptions(it.throwable)
        }
    }

    override fun getLayoutRes(): Int = R.layout.activity_my_work

    override fun setView() {
        initStatusBarColor()
        super.setView()
    }

    override fun initialize() {
        initControl(ControlDrawableSpec.LEFT)
        initSubjectTab()
        binding.recycler.rv.adapter = adapter
        binding.recycler.rv.addItemDecoration(myDivider(R.color.line))

        workViewModel.myHomework.observe(this, observer)
    }

    override fun bindListener() {
        binding.workToolbar.setNavigationOnClickListener { onBackPressed() }

        adapter.loadMoreModule?.setOnLoadMoreListener {
            workViewModel.getHomeWork(pageInfo.getPageIndex())
        }

        binding.recycler.refresh.setOnRefreshListener {
            pageInfo.reloadData()
            workViewModel.getHomeWork(pageInfo.getPageIndex())
        }

        unfinished.setOnClickListener {
            initControl(ControlDrawableSpec.LEFT)
            workViewModel.status = EbdState.UNFINISHED
            pageInfo.reloadData()
            workViewModel.getHomeWork(pageInfo.getPageIndex())
        }

        finished.setOnClickListener {
            initControl(ControlDrawableSpec.RIGHT)
            workViewModel.status = EbdState.FINISHED
            pageInfo.reloadData()
            workViewModel.getHomeWork(pageInfo.getPageIndex())
        }

    }

    override fun onResume() {
        super.onResume()
        pageInfo.reloadData()
        workViewModel.getHomeWork(pageInfo.getPageIndex())
    }

    /**
     * 顶部指示器样式
     */
    private fun initControl(selected: Int) {
        when (selected) {
            ControlDrawableSpec.LEFT -> {
                binding.unfinished.isSelected = true
                binding.finished.isSelected = false
                binding.unfinished.setTextColor(ContextCompat.getColor(this, R.color.title))
                binding.finished.setTextColor(ContextCompat.getColor(this, R.color.white))

                binding.unfinished.isEnabled = false
                binding.finished.isEnabled = true
            }
            ControlDrawableSpec.RIGHT -> {
                binding.finished.isSelected = true
                binding.unfinished.isSelected = false
                binding.finished.setTextColor(ContextCompat.getColor(this, R.color.title))
                binding.unfinished.setTextColor(ContextCompat.getColor(this, R.color.white))

                binding.finished.isEnabled = false
                binding.unfinished.isEnabled = true
            }
        }
        binding.unfinished.background = ControlDrawableSpec.build(ControlDrawableSpec.LEFT, this)
        binding.finished.background = ControlDrawableSpec.build(ControlDrawableSpec.RIGHT, this)
    }

    /**
     * 顶部学科筛选
     */
    @SuppressLint("InflateParams")
    private fun initSubjectTab() {
        subjectList.add(User.Subject(null, "全部", 0))
        subjectList.addAll(getSubject())
        subjectList.forEach { subject ->
            val v = LayoutInflater.from(this).inflate(R.layout.item_tab_work, null)
            val tv: TextView = v.findViewById(R.id.tv_tab_item)
            tv.text = subject.Name
            binding.myWorkTab.addTab(binding.myWorkTab.newTab().setCustomView(v))
        }
        binding.myWorkTab.addOnTabSelectedListener(this)
    }

    override fun onTabReselected(mTab: TabLayout.Tab?) {
    }

    override fun onTabUnselected(mTab: TabLayout.Tab?) {
    }

    override fun onTabSelected(mTab: TabLayout.Tab) {
        if (mTab.position != 0) {
            workViewModel.subjectId = subjectList[mTab.position].Id
        } else {
            workViewModel.subjectId = null
        }
        pageInfo.reloadData()
        workViewModel.getHomeWork(pageInfo.getPageIndex())
    }
}
