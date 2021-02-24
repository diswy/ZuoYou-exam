package com.zuoyouxue.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.alibaba.android.arouter.launcher.ARouter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.diswy.foundation.base.adapter.BaseBindAdapter
import com.diswy.foundation.quick.loadCrop
import com.diswy.foundation.quick.toast
import com.ebd.common.Page
import com.ebd.common.config.DataKey
import com.ebd.common.config.EbdState
import com.ebd.common.vo.Period
import com.zuoyouxue.R
import com.zuoyouxue.databinding.ItemVideoPeriodBinding

class PeriodAdapter(private val showPic: Boolean = false) :
    BaseBindAdapter<Period>(R.layout.item_video_period) {

    private var mSelected: Int = -1// 当前选中的项,初次进来未选中

    init {
        setOnItemClickListener { _, _, pos ->
            val item = getItem(pos)
            when (item.Status) {
                EbdState.VIDEO_VOD -> ARouter.getInstance().build(Page.VIDEO_VOD)
                    .withInt(DataKey.Position, pos)
                    .withObject(DataKey.PeriodList, data)
                    .navigation()
                EbdState.VIDEO_LIVE -> {
                    ARouter.getInstance().build(Page.VIDEO_OBS_LIVE)
                        .withInt(DataKey.PeriodId, item.Id)
                        .navigation()
                }
                else -> {
                    context.toast("视频未准备好，还不能播放")
                }
            }
        }
    }

    override fun convert(helper: BaseViewHolder, item: Period) {
        helper.getBinding<ItemVideoPeriodBinding>()?.apply {
            period = item
            pos = helper.layoutPosition
            selected = mSelected
            executePendingBindings()

            val tvStatus: TextView = helper.getView(R.id.period_status)
            when (item.Status) {
                EbdState.VIDEO_LIVE -> {
                    tvStatus.text = "直播中"
                    tvStatus.setTextColor(ContextCompat.getColor(context, R.color.score))
                }
                EbdState.VIDEO_VOD -> {
                    tvStatus.text = "回放"
                    tvStatus.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
                }
                EbdState.VIDEO_LIVE_END -> {
                    tvStatus.text = "直播结束"
                    tvStatus.setTextColor(ContextCompat.getColor(context, R.color.implicit))
                }
                else -> {
                    tvStatus.text = "未开始"
                    tvStatus.setTextColor(ContextCompat.getColor(context, R.color.implicit))
                }
            }

            val ivScreen: ImageView = helper.getView(R.id.period_screen)
            if (showPic) {
                ivScreen.visibility = View.VISIBLE
                ivScreen.loadCrop(context, item.Snapshoot)
            } else {
                ivScreen.visibility = View.GONE
            }
        }
    }

    fun selected(pos: Int) {
        mSelected = pos
        notifyDataSetChanged()
    }
}