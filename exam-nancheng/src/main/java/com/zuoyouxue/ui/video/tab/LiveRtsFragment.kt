package com.zuoyouxue.ui.video.tab

import com.alibaba.android.arouter.facade.annotation.Route
import com.diswy.foundation.base.fragment.BaseBindFragment
import com.ebd.common.Page
import com.zuoyouxue.R
import com.zuoyouxue.databinding.FragmentLiveRtsBinding

/**
 * 白板互动直播
 */
@Route(path = Page.CHAT_RTS)
class LiveRtsFragment : BaseBindFragment<FragmentLiveRtsBinding>() {

    override fun getLayoutRes(): Int {
        return R.layout.fragment_live_rts
    }

    override fun initialize() {
    }
}