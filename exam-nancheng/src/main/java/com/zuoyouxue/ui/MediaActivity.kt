package com.zuoyouxue.ui

import android.graphics.Color
import android.view.WindowManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.diswy.foundation.base.activity.BaseBindActivity
import com.ebd.common.Page
import com.zuoyouxue.R
import com.zuoyouxue.databinding.ActivityMediaBinding
import kit.diswy.player.PlayerEvent

/**
 * 仅持有一个视频播放器，用于js通信，转为原生播放
 */
@Route(path = Page.MEDIA_SIMPLE)
class MediaActivity : BaseBindActivity<ActivityMediaBinding>(), PlayerEvent {

    @Autowired
    @JvmField
    var mediaUrl: String = ""

    override fun getLayoutRes(): Int = R.layout.activity_media

    override fun keepScreenOn(): Boolean = true

    override fun setView() {
        window.statusBarColor = Color.BLACK
        super.setView()
    }

    override fun initialize() {
        lifecycle.addObserver(binding.mediaPlayer)
        binding.mediaPlayer.addEventListener(this)

        if (mediaUrl.isNotEmpty()) {
            binding.mediaPlayer.play(this, mediaUrl)
        }
    }

    /**
     * 播放器
     */
    override fun isFull(isFull: Boolean) {
        toggleScreen(isFull)
    }

    override fun playerBack() {
        onBackPressed()
    }

    override fun onBackPressed() {
        if (binding.mediaPlayer.isFullScreen()) {
            binding.mediaPlayer.setFullScreen(false)
            toggleScreen(false)
        } else {
            super.onBackPressed()
        }
    }

    private fun toggleScreen(isFull: Boolean) {
        if (isFull) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
    }
}
