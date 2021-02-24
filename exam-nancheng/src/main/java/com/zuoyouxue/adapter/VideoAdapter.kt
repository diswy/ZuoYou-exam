package com.zuoyouxue.adapter

import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.diswy.foundation.base.adapter.BaseBindAdapter
import com.ebd.common.Page
import com.ebd.common.config.DataKey
import com.ebd.common.vo.Video
import com.ethanhua.skeleton.RecyclerViewSkeletonScreen
import com.ethanhua.skeleton.Skeleton
import com.zuoyouxue.R
import com.zuoyouxue.databinding.ItemVideoAllBinding

class VideoAdapter : BaseBindAdapter<Video>(R.layout.item_video_all) {

    init {
        setOnItemClickListener { _, _, position ->
            val itemData = data[position]
            ARouter.getInstance().build(Page.VIDEO)
                .withObject(DataKey.Video, itemData)
                .navigation()
        }
    }

    override fun convert(helper: BaseViewHolder, item: Video) {
        helper.getBinding<ItemVideoAllBinding>()?.apply {
            video = item
            executePendingBindings()
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