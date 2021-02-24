package com.zuoyouxue.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.alibaba.android.arouter.launcher.ARouter
import com.ebd.common.Page
import com.ebd.common.config.DataKey
import com.ebd.common.config.Ebd
import com.ebd.common.vo.WrongQuestion

class WrongPagerAdapter(
    activity: FragmentActivity,
    private val questionTaskId: Int,
    private val list: List<WrongQuestion.WrongQuestionItem>
) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int {
        return list.size
    }

    override fun createFragment(position: Int): Fragment {
        val url = Ebd.URL_WRONG.format(list[position].querstionId, questionTaskId)
        return ARouter.getInstance().build(Page.WRONG_DETAILS)
            .withString(DataKey.WrongUrl, url)
            .navigation() as Fragment
    }
}