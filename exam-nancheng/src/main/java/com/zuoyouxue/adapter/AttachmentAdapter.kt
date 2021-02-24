package com.zuoyouxue.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.ebd.common.vo.Attachment
import com.zuoyouxue.R

class AttachmentAdapter : BaseQuickAdapter<Attachment, BaseViewHolder>(R.layout.item_attachment) {

    override fun convert(helper: BaseViewHolder, item: Attachment) {
        helper.setText(R.id.attachment_btn, item.Name)
    }
}