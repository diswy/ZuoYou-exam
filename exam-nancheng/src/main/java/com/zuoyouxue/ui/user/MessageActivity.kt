package com.zuoyouxue.ui.user

import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.diswy.foundation.base.PageInfo
import com.diswy.foundation.base.activity.BaseToolbarBindActivity
import com.diswy.foundation.web.Resource
import com.diswy.foundation.web.Status
import com.ebd.common.App
import com.ebd.common.config.Ebd
import com.ebd.common.viewmodel.CommonViewModel
import com.ebd.common.vo.Message
import com.ebd.common.vo.MsgData
import com.zuoyouxue.R
import com.zuoyouxue.adapter.MessageAdapter
import com.zuoyouxue.databinding.ActivityMessageBinding
import com.zuoyouxue.ui.EbdWebActivity

class MessageActivity : BaseToolbarBindActivity<ActivityMessageBinding>() {

    private val adapter = MessageAdapter()
    private val pageInfo: PageInfo<MsgData> = PageInfo(adapter)

    private val cvm: CommonViewModel by viewModels { App.instance.factory }

    private val observer = Observer<Resource<Message>> {
        when (it.status) {
            Status.LOADING -> {
            }
            Status.SUCCESS -> {
                binding.recycler.refresh.finishRefresh()
                pageInfo.loadData(it.data?.dataList)
            }
            Status.ERROR -> handleExceptions(it.throwable)
        }
    }

    override fun pageTitle(): String = "消息中心"

    override fun getLayoutRes(): Int = R.layout.activity_message

    override fun initialize() {
        binding.recycler.rv.adapter = adapter

        cvm.message.observe(this, observer)
        cvm.getMsgList(pageInfo.getPageIndex())
    }

    override fun bindListener() {
        adapter.loadMoreModule?.setOnLoadMoreListener {
            cvm.getMsgList(pageInfo.getPageIndex())
        }

        binding.recycler.refresh.setOnRefreshListener {
            pageInfo.reloadData()
            cvm.getMsgList(pageInfo.getPageIndex())
        }

        adapter.setOnItemClickListener { _, _, position ->
            val itemData = adapter.data[position]
            cvm.readMessage(itemData.Type, itemData.Id)
            itemData.Status = 1
            adapter.notifyItemChanged(position)
            EbdWebActivity.start(this, Ebd.URL_MESSAGE.format(itemData.Id), "消息详情")
        }
    }

}
