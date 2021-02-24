package com.zuoyouxue.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.alibaba.android.arouter.launcher.ARouter
import com.ebd.common.config.DataKey
import com.ebd.common.vo.local.ObsLiveItem

class ObsLiveAdapter(
    activity: FragmentActivity,
    private val list: List<ObsLiveItem>
) :
    FragmentStateAdapter(activity) {

    override fun getItemCount(): Int {
        return list.size
    }

    override fun createFragment(position: Int): Fragment {
        return ARouter.getInstance().build(list[position].pagePath)
            .withString(DataKey.ChatRoomId, list[position].roomId)
            .navigation() as Fragment
    }
}