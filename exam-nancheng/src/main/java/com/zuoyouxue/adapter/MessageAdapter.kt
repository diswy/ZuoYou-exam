package com.zuoyouxue.adapter

import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.diswy.foundation.base.adapter.BaseBindAdapter
import com.ebd.common.vo.MsgData
import com.zuoyouxue.R
import com.zuoyouxue.databinding.ItemSystemMessageBinding

class MessageAdapter : BaseBindAdapter<MsgData>(R.layout.item_system_message), LoadMoreModule {

    override fun convert(helper: BaseViewHolder, item: MsgData) {

        helper.getBinding<ItemSystemMessageBinding>()?.apply {
            message = item
            executePendingBindings()
        }

        var status = ""
        var color = ContextCompat.getColor(context, R.color.black)

        when (item.Status) {
            0 -> {
                status = "未阅读"
                color = ContextCompat.getColor(context, R.color.message_no_read)
            }
            1 -> {
                status = "已阅读"
                color = ContextCompat.getColor(context, R.color.message_read)
            }
        }

        helper.setText(R.id.tv_message_status, status)
            .setTextColor(R.id.tv_message_status, color)
    }
}