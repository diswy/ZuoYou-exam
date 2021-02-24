package com.zuoyouxue.ui.homework

import android.widget.ImageView
import com.alibaba.android.arouter.launcher.ARouter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.diswy.foundation.base.activity.BaseToolbarBindActivity
import com.diswy.foundation.quick.load
import com.ebd.common.Page
import com.ebd.common.cache.getSubject
import com.ebd.common.config.DataKey
import com.ebd.common.vo.User
import com.zuoyouxue.R
import com.zuoyouxue.databinding.ActivityMyWrongBinding

/**
 * 错题本学科筛选页
 */
class MyWrongActivity : BaseToolbarBindActivity<ActivityMyWrongBinding>() {

    private val adapter =
        object : BaseQuickAdapter<User.Subject, BaseViewHolder>(R.layout.item_work_wrong) {
            override fun convert(helper: BaseViewHolder, item: User.Subject) {
                helper.setText(R.id.tv_subject_name, item.Name)
                val subjectImg = helper.getView<ImageView>(R.id.iv_subject)
                when (item.Name) {
                    "语文" -> subjectImg.load(context, R.drawable.ic_wrong_work_subject_cn)
                    "数学" -> subjectImg.load(context, R.drawable.ic_wrong_work_subject_math)
                    "英语" -> subjectImg.load(context, R.drawable.ic_wrong_work_subject_en)
                    "物理" -> subjectImg.load(context, R.drawable.ic_wrong_work_subject_physical)
                    "化学" -> subjectImg.load(context, R.drawable.ic_wrong_work_subject_chemical)
                    "音乐" -> subjectImg.load(context, R.drawable.ic_wrong_work_subject_music)
                    "历史" -> subjectImg.load(context, R.drawable.ic_wrong_work_subject_history)
                    "地理" -> subjectImg.load(context, R.drawable.ic_wrong_work_subject_geography)
                    "生物" -> subjectImg.load(context, R.drawable.ic_wrong_work_subject_biology)
                    "政治" -> subjectImg.load(context, R.drawable.ic_wrong_work_subject_politics)
                    "科学" -> subjectImg.load(context, R.drawable.ic_wrong_work_subject_science)
                    "寒假培优" -> subjectImg.load(context, R.drawable.ic_wrong_work_subject_all)
                    "综合" -> subjectImg.load(context, R.drawable.ic_wrong_work_subject_composite)
                    "信息技术" -> subjectImg.load(context, R.drawable.ic_wrong_work_subject_internet)
                }
            }
        }

    override fun emptyBackground(): Boolean = true

    override fun pageTitle(): String = "错题本"

    override fun getLayoutRes(): Int = R.layout.activity_my_wrong

    override fun initialize() {
        binding.wrongRecycler.adapter = adapter
        adapter.addData(getSubject())
    }

    override fun bindListener() {
        adapter.setOnItemClickListener { _, _, pos ->
            ARouter.getInstance().build(Page.WRONG)
                .withObject(DataKey.Subject, adapter.data[pos])
                .navigation()
        }
    }
}
