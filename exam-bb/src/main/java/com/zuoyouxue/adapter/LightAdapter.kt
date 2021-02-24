package com.zuoyouxue.adapter

import android.util.SparseArray
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.ebd.common.vo.QuestionInfo
import com.zuoyouxue.R

/**
 * 答题卡指示器
 */
class LightAdapter : BaseQuickAdapter<QuestionInfo, BaseViewHolder>(R.layout.item_answer_light) {

    private var myAnswer: SparseArray<String?>? = null
    private var mCurrentPos = 0

    override fun convert(helper: BaseViewHolder, item: QuestionInfo) {
        helper.setText(R.id.tv_pos, (helper.layoutPosition + 1).toString())

        val tv: TextView = helper.getView(R.id.tv_pos)

        val lp = FrameLayout.LayoutParams(-2, -2)
        when (helper.layoutPosition % 10) {
            0->lp.setMargins(0,5,0,0)
            1->lp.setMargins(0,15,0,0)
            2->lp.setMargins(0,25,0,0)
            3->lp.setMargins(0,35,0,0)
            4->lp.setMargins(0,45,0,0)
            5->lp.setMargins(0,45,0,0)
            6->lp.setMargins(0,35,0,0)
            7->lp.setMargins(0,25,0,0)
            8->lp.setMargins(0,15,0,0)
            9->lp.setMargins(0,5,0,0)
        }
        tv.layoutParams = lp

        if (mCurrentPos == helper.layoutPosition) {// 当前题号
            tv.setBackgroundResource(R.drawable.ic_light_current)
            tv.setTextColor(ContextCompat.getColor(context, R.color.white))
        } else {// 判断对应题目是否有答案
            myAnswer?.apply {
                if (get(item.Id) == null) {// 空答案
                    tv.setBackgroundResource(R.drawable.ic_light_normal)
                    tv.setTextColor(ContextCompat.getColor(context, R.color.title))
                } else {// 存在答案
                    tv.setBackgroundResource(R.drawable.ic_light_done)
                    tv.setTextColor(ContextCompat.getColor(context, R.color.white))
                }
            }
        }
    }

    /**
     * 刷新
     */
    fun updateLight(current: Int, newAnswer: SparseArray<String?>?) {
        mCurrentPos = current
        myAnswer = newAnswer
        notifyDataSetChanged()
    }

    /**
     * 获取未做完的题目数量
     */
    fun getUnDoCount(): Int {
        var count = 0
        data.forEach { item ->
            if (myAnswer?.get(item.Id).isNullOrEmpty()) {
                count++
            }
        }
        return count
    }

}