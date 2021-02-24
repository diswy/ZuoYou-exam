package com.zuoyouxue.ui.video

import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.diswy.foundation.base.activity.BaseToolbarBindActivity
import com.diswy.foundation.web.Resource
import com.diswy.foundation.web.Status
import com.ebd.common.App
import com.ebd.common.Page
import com.ebd.common.viewmodel.VideoViewModel
import com.ebd.common.vo.Period
import com.ebd.common.vo.Video
import com.zuoyouxue.R
import com.zuoyouxue.adapter.PeriodAdapter
import com.zuoyouxue.databinding.ActivityVideoBinding

@Route(path = Page.VIDEO)
class VideoActivity : BaseToolbarBindActivity<ActivityVideoBinding>() {

    @Autowired
    @JvmField
    var video: Video? = null

    private val adapter = PeriodAdapter()

    private val videoViewModel: VideoViewModel by viewModels { App.instance.factory }

    private val periodObserver = Observer<Resource<List<Period>>> {
        when (it.status) {
            Status.LOADING -> {
            }
            Status.SUCCESS -> {
                adapter.setNewData(it.data as MutableList)
            }
            Status.ERROR -> handleExceptions(it.throwable)
        }
    }

    override fun pageTitle(): String = video!!.Name

    override fun getLayoutRes(): Int = R.layout.activity_video

    override fun initialize() {
        binding.video = video
        binding.vodRv.adapter = adapter
        binding.vodRv.addItemDecoration(myDivider(R.color.line))
        adapter.setEmptyView(R.layout.empty_layout_no_course)

        videoViewModel.period.observe(this, periodObserver)

        videoViewModel.getPeriod(video!!.Id)
    }

    override fun bindListener() {

    }

}
