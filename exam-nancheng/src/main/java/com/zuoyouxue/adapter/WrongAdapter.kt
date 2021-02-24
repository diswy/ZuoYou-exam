package com.zuoyouxue.adapter

import android.widget.ImageView
import com.alibaba.android.arouter.launcher.ARouter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.diswy.foundation.base.adapter.BaseBindAdapter
import com.diswy.foundation.quick.load
import com.ebd.common.Page
import com.ebd.common.config.DataKey
import com.ebd.common.vo.MyWrong
import com.zuoyouxue.R
import com.zuoyouxue.databinding.ItemWorkWrongContentBinding

class WrongAdapter : BaseBindAdapter<MyWrong>(R.layout.item_work_wrong_content), LoadMoreModule {

    init {
        setOnItemClickListener { _, _, position ->
            val item = data[position]
            ARouter.getInstance().build(Page.WRONG_PAPER)
                .withObject(DataKey.Wrong, item)
                .navigation()
        }
    }


    override fun convert(helper: BaseViewHolder, item: MyWrong) {
        helper.getBinding<ItemWorkWrongContentBinding>()?.apply {
            myWrong = item
            executePendingBindings()
        }

        val subjectIcon = when (item.SubjectTypeName) {
            "语文" -> R.drawable.ic_work_subject_chinese
            "数学" -> R.drawable.ic_work_subject_math
            "英语" -> R.drawable.ic_work_subject_english
            "物理" -> R.drawable.ic_work_subject_physics
            "化学" -> R.drawable.ic_work_subject_chemistry
            "音乐" -> R.drawable.ic_work_subject_music
            "历史" -> R.drawable.ic_wrong_work_subject_history
            "地理" -> R.drawable.ic_wrong_work_subject_geography
            "生物" -> R.drawable.ic_wrong_work_subject_biology
            "政治" -> R.drawable.ic_wrong_work_subject_politics
            "科学" -> R.drawable.ic_wrong_work_subject_science
            "寒假培优" -> R.drawable.ic_wrong_work_subject_all
            "综合" -> R.drawable.ic_wrong_work_subject_composite
            "信息技术" -> R.drawable.ic_wrong_work_subject_internet
            else -> R.drawable.ic_work_subject_all
        }
        helper.getView<ImageView>(R.id.iv_wrong_subject_img).load(context, subjectIcon)
    }
}