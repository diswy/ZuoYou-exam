package com.zuoyouxue.ui.homework

import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.diswy.foundation.base.PageInfo
import com.diswy.foundation.base.activity.BaseToolbarBindActivity
import com.diswy.foundation.web.Resource
import com.diswy.foundation.web.Status
import com.ebd.common.App
import com.ebd.common.Page
import com.ebd.common.viewmodel.WorkViewModel
import com.ebd.common.vo.MyWrong
import com.ebd.common.vo.User
import com.zuoyouxue.R
import com.zuoyouxue.adapter.WrongAdapter
import com.zuoyouxue.databinding.ActivityWrongContentBinding

/**
 * 错题本学科下的题目页
 */
@Route(path = Page.WRONG)
class WrongContentActivity : BaseToolbarBindActivity<ActivityWrongContentBinding>() {

    @Autowired
    @JvmField
    var subject: User.Subject? = null

    private val adapter = WrongAdapter()
    private val pageInfo: PageInfo<MyWrong> = PageInfo(adapter)

    private val workViewModel: WorkViewModel by viewModels { App.instance.factory }

    private val observer = Observer<Resource<List<MyWrong>>> {
        when (it.status) {
            Status.LOADING -> {
            }
            Status.SUCCESS -> {
                binding.recycler.refresh.finishRefresh()
                pageInfo.loadData(it.data)
            }
            Status.ERROR -> handleExceptions(it.throwable)
        }
    }

    override fun pageTitle(): String = subject!!.Name

    override fun getLayoutRes(): Int = R.layout.activity_wrong_content

    override fun initialize() {
        binding.recycler.rv.adapter = adapter
        binding.recycler.rv.addItemDecoration(myDivider(R.color.line))
        adapter.setEmptyView(R.layout.empty_layout_no_error)

        workViewModel.myWrong.observe(this, observer)
        workViewModel.getWrongList(pageInfo.getPageIndex(), subject!!.Id)
    }

    override fun bindListener() {
        adapter.loadMoreModule?.setOnLoadMoreListener {
            workViewModel.getWrongList(pageInfo.getPageIndex(), subject!!.Id)
        }

        binding.recycler.refresh.setOnRefreshListener {
            pageInfo.reloadData()
            workViewModel.getWrongList(pageInfo.getPageIndex(), subject!!.Id)
        }
    }

}
