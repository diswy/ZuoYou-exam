package com.zuoyouxue.adapter

import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.chad.library.adapter.base.BaseDelegateMultiAdapter
import com.chad.library.adapter.base.delegate.BaseMultiTypeDelegate
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.diswy.foundation.quick.load
import com.ebd.common.Page
import com.ebd.common.config.DataKey
import com.ebd.common.config.Ebd
import com.ebd.common.config.EbdState
import com.ebd.common.vo.Homework
import com.ethanhua.skeleton.RecyclerViewSkeletonScreen
import com.ethanhua.skeleton.Skeleton
import com.zuoyouxue.R
import com.zuoyouxue.databinding.ItemWorkFinishedBinding
import com.zuoyouxue.databinding.ItemWorkUnfinishedBinding
import com.zuoyouxue.ui.EbdWebActivity

/**
 * 作业列表、包含多种状态
 */
class HomeworkAdapter : BaseDelegateMultiAdapter<Homework, BaseViewHolder>(), LoadMoreModule {

    init {
        setMultiTypeDelegate(object : BaseMultiTypeDelegate<Homework>() {
            override fun getItemType(data: List<Homework>, position: Int): Int {
                return data[position].Status
            }
        })

        getMultiTypeDelegate()!!
            .addItemType(EbdState.NEW_WORK, R.layout.item_work_unfinished)
            .addItemType(EbdState.WORKING, R.layout.item_work_unfinished)
            .addItemType(EbdState.WORK_DOWN, R.layout.item_work_unfinished)
            .addItemType(EbdState.WORK_READ, R.layout.item_work_finished)

        setOnItemClickListener { _, _, position ->
            val itemData = data[position]
            when (itemData.Status) {
                EbdState.NEW_WORK, EbdState.WORKING -> {
                    if (itemData.attachments.isNullOrEmpty()) {// 没有媒体附件
                        // 新作业、作答中
                        val url = Ebd.URL_START_WORK.format(itemData.PapersId, itemData.TaskId)
                        EbdWebActivity.start(context, url, itemData)
                    } else {// 有媒体附件
                        ARouter.getInstance().build(Page.ATTACHMENT)
                            .withParcelable(DataKey.Homework, itemData)
                            .navigation()
                    }
                }
                EbdState.WORK_DOWN, EbdState.WORK_READ -> {
                    val url = Ebd.URL_END_WORK.format(itemData.TaskId)
                    EbdWebActivity.start(context, url, itemData.Name)
                }
            }
        }
    }

    override fun onItemViewHolderCreated(viewHolder: BaseViewHolder, viewType: Int) {
        DataBindingUtil.bind<ViewDataBinding>(viewHolder.itemView)
    }

    override fun convert(helper: BaseViewHolder, item: Homework) {
        when (helper.itemViewType) {
            EbdState.NEW_WORK, EbdState.WORKING, EbdState.WORK_DOWN -> {
                helper.getBinding<ItemWorkUnfinishedBinding>()?.apply {
                    homework = item
                    executePendingBindings()

                    val icon: ImageView = helper.getView(R.id.iv_subject_img)
                    val status: TextView = helper.getView(R.id.tv_status)
                    loadIcon(item.SubjectId, icon)
                    loadStatus(item.Status, status)
                }
            }
            EbdState.WORK_READ -> {
                helper.getBinding<ItemWorkFinishedBinding>()?.apply {
                    homework = item
                    executePendingBindings()

                    val icon: ImageView = helper.getView(R.id.iv_subject_img)
                    loadIcon(item.SubjectId, icon)
                }
            }
        }
    }

    private fun loadIcon(subjectId: Int, icon: ImageView) {
        when (subjectId) {
            EbdState.Chinese -> icon.load(context, R.drawable.ic_work_subject_chinese)
            EbdState.Math -> icon.load(context, R.drawable.ic_work_subject_math)
            EbdState.English -> icon.load(context, R.drawable.ic_work_subject_english)
            EbdState.Physics -> icon.load(context, R.drawable.ic_work_subject_physics)
            EbdState.Chemistry -> icon.load(context, R.drawable.ic_work_subject_chemistry)
            EbdState.Music -> icon.load(context, R.drawable.ic_work_subject_music)
            EbdState.History -> icon.load(context, R.drawable.ic_work_subject_history)
            EbdState.Geography -> icon.load(context, R.drawable.ic_work_subject_geography)
            EbdState.Biology -> icon.load(context, R.drawable.ic_work_subject_biology)
            EbdState.Politics -> icon.load(context, R.drawable.ic_work_subject_politics)
            EbdState.Science -> icon.load(context, R.drawable.ic_work_subject_science)
            EbdState.all -> icon.load(context, R.drawable.ic_work_subject_all)
            EbdState.Composite -> icon.load(context, R.drawable.ic_work_subject_composite)
            EbdState.Internet -> icon.load(context, R.drawable.ic_work_subject_internet)
        }
    }

    private fun loadStatus(status: Int, tv: TextView) {
        when (status) {
            EbdState.NEW_WORK -> tv.setTextColor(ContextCompat.getColor(context, R.color.newWork))
            EbdState.WORKING -> tv.setTextColor(ContextCompat.getColor(context, R.color.working))
            EbdState.WORK_DOWN -> tv.setTextColor(ContextCompat.getColor(context, R.color.finished))
        }
    }

    private lateinit var skeletonScreen: RecyclerViewSkeletonScreen
    /**
     * 骨架屏配置参数
     * shimmer(true)    // whether show shimmer animation.                      default is true
     * count(10)        // the recycler view item count.                        default is 10
     * color(color)     // the shimmer color.                                   default is #a2878787
     * angle(20)        // the shimmer angle.                                   default is 20
     * duration(1000)   // the shimmer animation duration.                      default is 1000
     * frozen(false)    // whether frozen recyclerView during skeleton showing  default is true
     */
    fun showSkeleton(rv: RecyclerView, @LayoutRes holderLayout: Int) {
        if (::skeletonScreen.isInitialized) {
            skeletonScreen.show()
        } else {
            skeletonScreen = Skeleton.bind(rv)
                .adapter(this)
                .load(holderLayout)
                .shimmer(false)
                .show()
        }
    }

    fun hideSkeleton() {
        if (::skeletonScreen.isInitialized) {
            skeletonScreen.hide()
        }
    }
}