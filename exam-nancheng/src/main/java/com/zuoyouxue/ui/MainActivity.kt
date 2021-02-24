package com.zuoyouxue.ui

import android.view.KeyEvent
import androidx.activity.viewModels
import androidx.fragment.app.commit
import androidx.lifecycle.observe
import com.alibaba.android.arouter.facade.annotation.Route
import com.ashokvarma.bottomnavigation.BottomNavigationBar
import com.ashokvarma.bottomnavigation.BottomNavigationItem
import com.diswy.foundation.base.activity.BaseBindActivity
import com.diswy.foundation.quick.toast
import com.ebd.common.App
import com.ebd.common.Page
import com.ebd.common.viewmodel.ToolViewModel
import com.zuoyouxue.R
import com.zuoyouxue.databinding.ActivityMainBinding
import com.zuoyouxue.ui.home.HomeFragment
import com.zuoyouxue.ui.homework.MyWorkFragment
import com.zuoyouxue.ui.user.MineFragment
import com.zuoyouxue.ui.video.VideoFragment
import kotlin.system.exitProcess


@Route(path = Page.HOME)
class MainActivity : BaseBindActivity<ActivityMainBinding>(),
    BottomNavigationBar.OnTabSelectedListener {

    private val toolViewModel: ToolViewModel by viewModels { App.instance.factory }

    override fun statusDarkMode(): Boolean = true

    override fun onTabReselected(position: Int) {}

    override fun onTabUnselected(position: Int) {}

    override fun onTabSelected(position: Int) {
        replaceFragments(position)
    }

    override fun getLayoutRes(): Int = R.layout.activity_main

    override fun initialize() {
        binding.mainTab
            .addItem(
                BottomNavigationItem(R.drawable.tab_home_active, "首页")
                    .setInactiveIconResource(R.drawable.tab_home_inactive)
            )
            .addItem(
                BottomNavigationItem(R.drawable.tab_video_active, "直播")
                    .setInactiveIconResource(R.drawable.tab_video_inactive)
            )
            .addItem(
                BottomNavigationItem(R.drawable.tab_work_active, "作业")
                    .setInactiveIconResource(R.drawable.tab_work_inactive)
            )
            .addItem(
                BottomNavigationItem(R.drawable.tab_mine_active, "我的")
                    .setInactiveIconResource(R.drawable.tab_mine_inactive)
            )
            .setBarBackgroundColor(R.color.white)
            .setMode(BottomNavigationBar.MODE_FIXED)
            .initialise()

        binding.mainTab.selectTab(0)

        binding.mainTab.setTabSelectedListener(this@MainActivity)
        replaceFragments(0)

        toolViewModel.userLog()

        // 平板版我的作业与视频改为fragment，直接切换底部Tab
        toolViewModel.switchWorkTab.observe(this) {
            if (it){
                binding.mainTab.selectTab(2)
            }
        }
        toolViewModel.switchVideoTab.observe(this) {
            if (it){
                binding.mainTab.selectTab(1)
            }
        }
    }

    private fun replaceFragments(pos: Int) {
        supportFragmentManager.commit {
            when (pos) {
                0 -> replace(R.id.container, HomeFragment())
                1 -> replace(R.id.container, VideoFragment())
                2 -> replace(R.id.container, MyWorkFragment())
                3 -> replace(R.id.container, MineFragment())
            }
        }
    }

    private var exitTime: Long = 0
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                toast("再按一次退出点点课")
                exitTime = System.currentTimeMillis()
            } else {
                finish()
                exitProcess(0)
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}
