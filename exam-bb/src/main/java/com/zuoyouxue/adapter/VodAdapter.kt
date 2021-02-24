package com.zuoyouxue.adapter

import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.airbnb.lottie.LottieAnimationView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.diswy.foundation.tools.TimeKitJ
import com.ebd.common.config.EbdState
import com.ebd.common.vo.Period
import com.zuoyouxue.R

class VodAdapter : BaseQuickAdapter<Period, BaseViewHolder>(R.layout.item_video_vod) {

    private var mSelected: Int = -1// 当前选中的项,初次进来未选中

    override fun convert(helper: BaseViewHolder, item: Period) {
        val tvStatus: TextView = helper.getView(R.id.vod_status)
        val tvName: TextView = helper.getView(R.id.vod_name)
        val tvTeacher: TextView = helper.getView(R.id.vod_teacher)
        val tvTime: TextView = helper.getView(R.id.vod_time)
        val animationPlaying: LottieAnimationView = helper.getView(R.id.vod_playing)
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

        tvName.text = item.Name
        tvTeacher.text = context.getString(R.string.video_teacher, item.TeacherName)
        tvTime.text = context.getString(R.string.vod_time, TimeKitJ.YMDHM(item.PlanStartDate))

        if (mSelected == helper.layoutPosition) {
            tvName.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
            tvTeacher.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
            tvTime.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
            animationPlaying.visibility = View.VISIBLE
        } else {
            tvName.setTextColor(ContextCompat.getColor(context, R.color.title))
            tvTeacher.setTextColor(ContextCompat.getColor(context, R.color.implicit))
            tvTime.setTextColor(ContextCompat.getColor(context, R.color.implicit))
            animationPlaying.visibility = View.GONE
        }

    }

    fun selected(pos: Int) {
        mSelected = pos
        notifyDataSetChanged()
    }
}