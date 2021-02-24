package com.zuoyouxue.adapter

import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.diswy.foundation.base.adapter.BaseBindAdapter
import com.diswy.foundation.quick.*
import com.ebd.common.App
import com.ebd.common.config.Ebd
import com.ebd.common.config.EbdState
import com.ebd.common.vo.SharedWork
import com.ebd.common.vo.local.SAnswer
import com.zuoyouxue.R
import com.zuoyouxue.databinding.ItemWorkSharedBinding
import com.zuoyouxue.ui.EbdWebActivity
import com.zuoyouxue.ui.homework.card.BlankCard

class SharedAdapter : BaseBindAdapter<SharedWork.SharedWorkItem>(R.layout.item_work_shared),
    LoadMoreModule {

    init {
        setOnItemClickListener { _, _, position ->
            val url = Ebd.URL_SHARED.format(data[position].Id)
            EbdWebActivity.start(context, url, data[position].PapersName)
        }
    }

    override fun convert(helper: BaseViewHolder, item: SharedWork.SharedWorkItem) {
        helper.getBinding<ItemWorkSharedBinding>()?.apply {
            shareWork = item
            executePendingBindings()
        }

        val subjectIcon = when (item.SubjectTypeId) {
            EbdState.Chemistry -> R.drawable.ic_work_subject_chemistry
            EbdState.Math -> R.drawable.ic_work_subject_math
            EbdState.Physics -> R.drawable.ic_work_subject_physics
            EbdState.English -> R.drawable.ic_work_subject_english
            EbdState.Chinese -> R.drawable.ic_work_subject_chinese
            else -> null
        }
        helper.getView<ImageView>(R.id.share_work_subject_img).load(context, subjectIcon)
        // 动态视图
        val answerGroup: LinearLayout = helper.getView(R.id.share_work_answer_group)
        answerGroup.removeAllViews()
        // 学生已经答题，初始化答案
        if (!item.AnswerHtml.isNullOrEmpty()) {
            try {
                val answerList = App.instance.globalGson.fromJsonArray(item.AnswerHtml!!, SAnswer::class.java)
                answerList.sortBy { it.Id }

                for (i in 0 until answerList.size) {// 可能会有多个空答案
                    // 添加文字答案
                    val numFormat = "%d、"
                    val tv = TextView(context)
                    tv.text = if (answerList.size > 1) {
                        numFormat.format(answerList[i].Answer.trimByRegex(BlankCard.imgRegex))
                    } else {
                        answerList[i].Answer.trimByRegex(BlankCard.imgRegex)
                    }
                    val lp = LinearLayout.LayoutParams(-2, -2)
                    answerGroup.addView(tv, lp)
                    // 添加图片答案
                    val imgUrl = answerList[i].Answer.getImgTagSrcContent()
                    if (imgUrl.isNotEmpty()) {
                        val iv = ImageView(context)
                        iv.load(context, imgUrl)
                        val lpIv = LinearLayout.LayoutParams(context.dip(170), context.dip(170))
                        answerGroup.addView(iv, lpIv)
                    }
                }
            } catch (e: Exception) {
                // TODO 捕获错误
            }
        }
    }
}