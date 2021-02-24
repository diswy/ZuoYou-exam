package kit.diswy.player

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.diswy.foundation.quick.dip
import com.diswy.kit.R
import com.diswy.kit.databinding.PlayerLiveViewBinding
import com.netease.neliveplayer.playerkit.sdk.LivePlayer
import com.netease.neliveplayer.playerkit.sdk.PlayerManager
import com.netease.neliveplayer.playerkit.sdk.model.SDKOptions
import com.netease.neliveplayer.playerkit.sdk.model.VideoBufferStrategy
import com.netease.neliveplayer.playerkit.sdk.model.VideoOptions
import com.netease.neliveplayer.playerkit.sdk.model.VideoScaleMode
import com.netease.neliveplayer.proxy.config.NEPlayerConfig

class LivePlayer @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), LifecycleObserver {

    companion object {
        const val HIDE_CONTROL = 0x02
    }

    private val binding: PlayerLiveViewBinding =
        DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.player_live_view, this, true)
    private val options: VideoOptions = VideoOptions().apply {
        hardwareDecode = true
        bufferStrategy = VideoBufferStrategy.FLUENCY
        isPlayLongTimeBackground = false
    }

    private var player: LivePlayer? = null
    private var eventListener: PlayerEvent? = null

    private val mHandler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                HIDE_CONTROL -> {
                    toggleControl()
                }
            }
        }
    }

    /**
     * 事件传递
     */
    fun addEventListener(mEventListener: PlayerEvent) {
        eventListener = mEventListener
    }

    private val playerObserver = object : MyLivePlayerObserver {
        override fun onPreparing() {
            loading()
        }

        override fun onFirstVideoRendered() {
            loading(false)
            mHandler.sendEmptyMessageDelayed(HIDE_CONTROL, 3000)
        }
    }

    init {
        val config = SDKOptions().apply {
            privateConfig = NEPlayerConfig()
        }
        PlayerManager.init(context, config)

        initFullScreen()
        binding.playBack.setOnClickListener {
            eventListener?.playerBack()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private fun lifeOnResume() {
        player?.onActivityResume(false)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    private fun lifeOnPause() {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private fun lifeOnStop() {
        player?.onActivityStop(true)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun lifeOnDestroy() {
        mHandler.removeCallbacksAndMessages(null)
        releasePlayer()
    }

    fun play(ctx: Context, path: String) {
        releasePlayer()
        player = PlayerManager.buildLivePlayer(ctx, path, options)
        player?.registerPlayerObserver(playerObserver, true)
        player?.start()
        player?.setupRenderView(binding.liveTexture, VideoScaleMode.FIT)
    }

    /**
     * 释放播放器
     */
    private fun releasePlayer() {
        player?.apply {
            registerPlayerObserver(playerObserver, false)
            setupRenderView(null, VideoScaleMode.NONE)
            binding.liveTexture.releaseSurface()
            stop()
        }
        player = null
    }

    /**
     * 缓冲动画
     */
    private fun loading(show: Boolean = true) {
        binding.playerMask.visibility = if (show) View.VISIBLE else View.GONE
        binding.playerLoading.visibility = if (show) View.VISIBLE else View.GONE
    }

    /**
     * 全屏切换
     */
    private fun initFullScreen() {
        binding.playFull.setOnClickListener {
            mHandler.removeMessages(HIDE_CONTROL)
            mHandler.sendEmptyMessageDelayed(HIDE_CONTROL, 3000)
            binding.playFull.visibility = View.GONE
            eventListener?.isFull(true)
        }
    }

    /**
     * 动画管理
     */
    private var mControlShow = true

    private fun toggleControl() {
        mHandler.removeMessages(HIDE_CONTROL)
        if (mControlShow) {
            binding.liveBottomControl.animate()
                .translationY(context.dip(48).toFloat())
                .alpha(0f)
                .duration = 200
            binding.liveTopControl.animate()
                .translationY(-context.dip(48).toFloat())
                .alpha(0f)
                .duration = 200
        } else {
            binding.liveBottomControl.animate()
                .translationY(0f)
                .alpha(1f)
                .duration = 200
            binding.liveTopControl.animate()
                .translationY(0f)
                .alpha(1f)
                .duration = 200
            mHandler.sendEmptyMessageDelayed(HIDE_CONTROL, 3000)
        }
        mControlShow = !mControlShow
    }

    /**
     * @return 播放器当前是否处于全拼模式
     */
    fun isFullScreen(): Boolean {
        return !binding.playFull.isVisible
    }

    fun setFullScreen(isFull: Boolean) {
        binding.playFull.visibility = if (isFull) View.GONE else View.VISIBLE
    }
}