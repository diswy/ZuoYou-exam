package com.zuoyouxue.adapter

import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.diswy.foundation.quick.load
import com.diswy.foundation.tools.FileHelper
import com.diswy.foundation.tools.TimeKit
import com.diswy.foundation.view.FancyButton
import com.ebd.common.vo.DocItem
import com.zuoyouxue.R
import timber.log.Timber
import java.text.DecimalFormat

class FileAdapter : BaseQuickAdapter<DocItem, BaseViewHolder>(R.layout.item_file_doc),
    LoadMoreModule {

    override fun convert(helper: BaseViewHolder, item: DocItem) {
        val icon: ImageView = helper.getView(R.id.file_img)
        val button: FancyButton = helper.getView(R.id.file_button)
        when (item.ExtensionName) {
            ".doc", ".docx" -> icon.load(context, R.drawable.ic_file_word)
            ".jpg", ".png" -> icon.load(context, R.drawable.ic_file_img)
            ".ppt", ".pptx" -> icon.load(context, R.drawable.ic_file_ppt)
            ".xls", ".xlsx" -> icon.load(context, R.drawable.ic_file_excel)
            ".zip", ".rar" -> icon.load(context, R.drawable.ic_file_rar)
            ".txt" -> icon.load(context, R.drawable.ic_file_text)
            ".mp3" -> icon.load(context, R.drawable.ic_file_mp3)
            ".mp4" -> icon.load(context, R.drawable.ic_file_mp4)
            else -> icon.load(context, R.drawable.ic_file_unknown)
        }
        val size: String = if (item.Size > 1024) {
            val kb: Float = item.Size % 1024 / 1024f
            val mb: Int = item.Size / 1024
            "${DecimalFormat(".00").format(mb + kb)}Mb"
        } else {
            "${item.Size}Kb"
        }
        val description = "%s    %s".format(TimeKit.YMDHM(item.CreateDateTime), size)
        helper.setText(R.id.file_name, item.FileName)
            .setText(R.id.file_push_time, description)

        Timber.d("文件名：${item.FileName}")
        if (FileHelper.isFileExists(item.FileName)) {
            button.text = "打开"
        } else {
            button.text = "下载"
        }

    }
}