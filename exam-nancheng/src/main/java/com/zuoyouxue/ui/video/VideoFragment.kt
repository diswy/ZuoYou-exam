package com.zuoyouxue.ui.video


import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.diswy.foundation.base.fragment.BaseBindFragment
import com.diswy.foundation.web.Resource
import com.diswy.foundation.web.Status
import com.ebd.common.App
import com.ebd.common.cache.getSubject
import com.ebd.common.viewmodel.VideoViewModel
import com.ebd.common.vo.User
import com.ebd.common.vo.Video
import com.google.android.material.tabs.TabLayout
import com.zuoyouxue.R
import com.zuoyouxue.adapter.VideoAdapter
import com.zuoyouxue.databinding.FragmentVideoBinding


/**
 * 视频列表
 */
class VideoFragment : BaseBindFragment<FragmentVideoBinding>(),
    TabLayout.OnTabSelectedListener {

    private val adapter = VideoAdapter()
    private val videoViewModel: VideoViewModel by viewModels { App.instance.factory }
    private val subjectList = ArrayList<User.Subject>()// 学科

    private val observer = Observer<Resource<List<Video>>> {
        when (it.status) {
            Status.LOADING -> {
                adapter.setNewData(null)
                adapter.showSkeleton(binding.videoRecycler, R.layout.skeleton_item_video)
            }
            Status.SUCCESS -> {
                adapter.hideSkeleton()
                binding.refresh.finishRefresh()
                adapter.setNewData(it.data as MutableList)
            }
            Status.ERROR -> handleExceptions(it.throwable)
        }
    }

    override fun getLayoutRes(): Int = R.layout.fragment_video

    override fun initialize() {
        initSubjectTab()
        binding.videoRecycler.adapter = adapter

        videoViewModel.video.observe(this, observer)
        videoViewModel.getVideo()
    }

    override fun bindListener() {
        binding.refresh.setOnRefreshListener {
            videoViewModel.getVideo()
        }
    }

    /**
     * 顶部学科筛选
     */
    @SuppressLint("InflateParams")
    private fun initSubjectTab() {
        subjectList.add(User.Subject(null, "全部", 0))
        subjectList.addAll(getSubject())
        subjectList.forEach { subject ->
            val v = LayoutInflater.from(requireContext()).inflate(R.layout.item_tab_work, null)
            val tv: TextView = v.findViewById(R.id.tv_tab_item)
            tv.text = subject.Name
            binding.myVideoTab.addTab(binding.myVideoTab.newTab().setCustomView(v))
        }
        binding.myVideoTab.addOnTabSelectedListener(this)
    }

    override fun onTabReselected(mTab: TabLayout.Tab?) {
    }

    override fun onTabUnselected(mTab: TabLayout.Tab?) {
    }

    override fun onTabSelected(mTab: TabLayout.Tab) {
        if (mTab.position != 0) {
            videoViewModel.subjectId = subjectList[mTab.position].Id
        } else {
            videoViewModel.subjectId = null
        }
        videoViewModel.getVideo()
    }

}
