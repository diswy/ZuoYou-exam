package kit.diswy.player

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.SeekBar
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.diswy.foundation.quick.dip
import com.diswy.foundation.quick.onClick
import com.diswy.foundation.quick.toast
import com.diswy.kit.R
import com.diswy.kit.databinding.PlayerViewBinding
import com.netease.neliveplayer.playerkit.sdk.LivePlayer
import com.netease.neliveplayer.playerkit.sdk.PlayerManager
import com.netease.neliveplayer.playerkit.sdk.VodPlayer
import com.netease.neliveplayer.playerkit.sdk.model.*
import com.netease.neliveplayer.proxy.config.NEPlayerConfig
import kit.diswy.vo.VodDefinition
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList

/**
 * 点播播放器，基于网易云
 */
class VodPlayer @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), LifecycleObserver {

    companion object {
        const val SHOW_PROGRESS = 0x01
        const val HIDE_CONTROL = 0x02
    }

    private val binding: PlayerViewBinding =
        DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.player_view, this, true)
    private val options: VideoOptions = VideoOptions().apply {
        hardwareDecode = true
        bufferStrategy = VideoBufferStrategy.ANTI_JITTER
    }

    private var player: VodPlayer? = null
    private var eventListener: PlayerEvent? = null
    private var statusListener: MyPlayerStatusObserver? = null
    private var recordListener: ((position: Long) -> Unit)? = null
    private var totalDuration: Long = 0
    private val mDefinitionList = ArrayList<VodDefinition>()// 清晰度集合
    private var mSpeed: Float = 1.0f// 播放速度
    private var mSpeedText: String = "倍数"
    private var recordDownTo: Int = 0// 记录显示倒计时
    private var mp3Only: Boolean = false
    private var curPath: String = ""// 当前播放地址

    /**
     * 播放记录监听
     */
    fun addRecordListener(listener: (position: Long) -> Unit) {
        recordListener = listener
    }

    /**
     * 事件传递
     */
    fun addEventListener(mEventListener: PlayerEvent) {
        eventListener = mEventListener
    }

    /**
     * 播放状态监听
     */
    fun addStatusListener(mStatusListener: MyPlayerStatusObserver) {
        statusListener = mStatusListener
    }

    private val mHandler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                HIDE_CONTROL -> {
                    toggleControl()
                }
                SHOW_PROGRESS -> {
                    if (recordDownTo > 0) {
                        recordDownTo--
                    } else {
                        binding.playRecord.visibility = View.GONE
                    }
                    val position = setProgress()
                    recordListener?.invoke(position)
                    sendMessageDelayed(obtainMessage(SHOW_PROGRESS), 1000 - position % 1000)
                }
            }
        }
    }

    private val playerObserver = object : MyVodPlayerObserver {
        override fun onPreparing() {
            loading()
        }

        override fun onFirstVideoRendered() {
            binding.playMusic.visibility = View.GONE
            binding.playPause.frame = 29
            statusListener?.onPlayerStart()
            loading(false)
            mHandler.sendEmptyMessageDelayed(HIDE_CONTROL, 3000)
        }

        override fun onFirstAudioRendered() {
            if (mp3Only) {
                binding.playMusic.visibility = View.VISIBLE
                loading(false)
            }
        }

        override fun onSeekCompleted() {
            mHandler.sendEmptyMessageDelayed(SHOW_PROGRESS, 1000)
        }

        override fun onNetStateBad() {
            statusListener?.onPlayerNetBad()
        }

        override fun onCompletion() {
            binding.playReplay.visibility = View.VISIBLE
            binding.playMusic.visibility = View.GONE
            statusListener?.onPlayerCompletion()
        }

        override fun onStateChanged(stateInfo: StateInfo) {
            if (stateInfo.state == LivePlayer.STATE.PAUSED) {
                statusListener?.onPlayerPause()
            }
        }
    }

    private val mProgressSeekListener = object : SeekBar.OnSeekBarChangeListener {

        override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
            val mCurrentTime = stringForTime(totalDuration * i / 100)
            val mEndTime = stringForTime(totalDuration)
            binding.playTime.text = "%s/%s".format(mCurrentTime, mEndTime)
        }

        override fun onStartTrackingTouch(seekBar: SeekBar) {
            mHandler.removeMessages(SHOW_PROGRESS)
            mHandler.removeMessages(HIDE_CONTROL)
        }

        override fun onStopTrackingTouch(seekBar: SeekBar) {
            mHandler.sendEmptyMessageDelayed(HIDE_CONTROL, 3000)
            player?.apply {
                seekTo(duration * seekBar.progress / 100)
            }
        }
    }

    init {
        val config = SDKOptions().apply {
            privateConfig = NEPlayerConfig()
        }
        PlayerManager.init(context, config)
        mHandler.sendEmptyMessage(SHOW_PROGRESS)

        binding.playBack.setOnClickListener {
            eventListener?.playerBack()
        }

        binding.playGroup.onClick(300) {
            if (mSpeedShow) {// 倍数播放选择
                toggleSpeed()
                return@onClick
            }
            if (mDefinitionShow) {// 清晰度选择
                toggleDefinition()
                return@onClick
            }
            toggleControl()
        }

        /**
         * 播放器
         * Play and Pause
         */
        binding.playPause.frame = 29
        binding.playPause.setOnClickListener {
            mHandler.removeMessages(HIDE_CONTROL)
            mHandler.sendEmptyMessageDelayed(HIDE_CONTROL, 3000)
            binding.playPause.isEnabled = false
            binding.playPause.resumeAnimation()
            player?.apply {
                if (isPlaying) pause() else start()
            }
        }

        binding.playPause.addAnimatorUpdateListener {
            if (binding.playPause.frame == 28) {
                binding.playPause.pauseAnimation()
                binding.playPause.isEnabled = true
                binding.playPause.frame = 29
            }

            if (binding.playPause.frame == 59) {
                binding.playPause.isEnabled = true
            }
        }

        /**
         * 进度条
         */
        binding.playSeek.setOnSeekBarChangeListener(mProgressSeekListener)

        binding.playDefinition.setOnClickListener {
            mHandler.removeMessages(HIDE_CONTROL)
            mHandler.sendEmptyMessageDelayed(HIDE_CONTROL, 3000)
            toggleDefinition()
        }

        /**
         * 重放
         */
        binding.playReplay.setOnClickListener {
            binding.playReplay.visibility = View.GONE
            play(context, curPath)
        }
        initSpeed()
        initFullScreen()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private fun lifeOnResume() {
        Timber.d("lifeOnResume")
        player?.onActivityResume(false)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    private fun lifeOnPause() {
        player?.pause()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private fun lifeOnStop() {
        player?.onActivityStop(false)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun lifeOnDestroy() {
        mHandler.removeCallbacksAndMessages(null)
        releasePlayer()
    }

    /**
     * 禁用拖动功能
     */
    fun disableSeek() {
        binding.playSeek.isEnabled = false
    }

    /**
     * 启用拖动功能
     */
    fun enableSeek() {
        binding.playSeek.isEnabled = true
    }

    /**
     * 显示
     */
    fun showPlayRecord(position: Long) {
        binding.playRecordText.text = "上次你观看到%s，".format(stringForTime(position))
        recordDownTo = 6
        binding.playRecord.visibility = View.VISIBLE
        binding.playRecord.setOnClickListener {
            player?.apply {
                seekTo(position)
            }
        }
    }

    /**
     * 多地址播放，可以选择清晰度
     */
    fun play(ctx: Context, list: ArrayList<VodDefinition>) {
        if (list.isEmpty()) {
            ctx.toast("没有获取到播放地址，请刷新网络重试")
            return
        }
        mDefinitionList.clear()
        mDefinitionList.addAll(list)
        initDefinition(ctx)
        play(ctx, 0)
    }

    private fun play(ctx: Context, pos: Int) {
        play(ctx, mDefinitionList[pos].playPath)
        binding.playDefinition.text = mDefinitionList[pos].definitionName
    }

    /**
     * 单地址播放
     */
    fun play(ctx: Context, path: String) {
        releasePlayer()// 释放，保证只有一个实例在播放
        curPath = path
        mp3Only = path.endsWith(".mp3")
        player = PlayerManager.buildVodPlayer(ctx, path, options)
        player?.registerPlayerObserver(playerObserver, true)
        player?.start()
        player?.setupRenderView(binding.playerTexture, VideoScaleMode.FIT)
        player?.setPlaybackSpeed(mSpeed)
        binding.playDefinition.visibility = if (mDefinitionList.isEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    /**
     * 释放播放器
     */
    private fun releasePlayer() {
        player?.apply {
            registerPlayerObserver(playerObserver, false)
            setupRenderView(null, VideoScaleMode.NONE)
            binding.playerTexture.releaseSurface()
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
     * 设置进度条
     */
    private fun setProgress(): Long {
        if (player == null) {
            return 0
        }
        val position = player!!.currentPosition
        val duration = player!!.duration
        totalDuration = duration
        var mEndTime = "--:--"
        if (duration > 0) {
            val pos = 100L * position / duration
            binding.playSeek.progress = pos.toInt()
            mEndTime = stringForTime(duration)
        }
        val mCurrentTime = stringForTime(position)
        binding.playTime.text = "%s/%s".format(mCurrentTime, mEndTime)
        return position
    }

    /**
     * 格式化时间
     */
    private fun stringForTime(position: Long): String {
        val totalSeconds = (position / 1000.0 + 0.5).toInt()
        val seconds = totalSeconds % 60
        val minutes = totalSeconds / 60 % 60
        val hours = totalSeconds / 3600
        return if (hours > 0) {
            String.format(Locale.CHINA, "%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format(Locale.CHINA, "%02d:%02d", minutes, seconds)
        }
    }

    /**
     * 动画管理
     */
    private var mControlShow = true
    private var mDefinitionShow = false
    private var mSpeedShow = false

    private fun toggleControl() {
        mHandler.removeMessages(HIDE_CONTROL)
        if (mControlShow) {
            binding.bottomControl.animate()
                .translationY(context.dip(48).toFloat())
                .alpha(0f)
                .duration = 200
            binding.playSeek.animate()
                .translationY(context.dip(48).toFloat())
                .alpha(0f)
                .duration = 200
            binding.topControl.animate()
                .translationY(-context.dip(48).toFloat())
                .alpha(0f)
                .duration = 200
        } else {
            binding.bottomControl.animate()
                .translationY(0f)
                .alpha(1f)
                .duration = 200
            binding.playSeek.animate()
                .translationY(0f)
                .alpha(1f)
                .duration = 200
            binding.topControl.animate()
                .translationY(0f)
                .alpha(1f)
                .duration = 200
            mHandler.sendEmptyMessageDelayed(HIDE_CONTROL, 3000)
        }
        mControlShow = !mControlShow
    }

    private fun toggleDefinition() {
        if (mDefinitionShow) {
            binding.playDefinitionControl.animate()
                .translationX(0f)
                .duration = 200
        } else {
            binding.playDefinitionControl.animate()
                .translationX(-context.dip(120).toFloat())
                .duration = 200
        }
        mDefinitionShow = !mDefinitionShow
    }

    private fun toggleSpeed() {
        if (mSpeedShow) {
            binding.playSpeedControl.root.animate()
                .translationX(0f)
                .duration = 200
        } else {
            binding.playSpeedControl.root.animate()
                .translationX(-context.dip(120).toFloat())
                .duration = 200
        }
        mSpeedShow = !mSpeedShow
    }

    /**
     * 清晰度管理
     */
    private fun initDefinition(ctx: Context) {
        val adapter = DefinitionAdapter(mDefinitionList)
        binding.playDefinitionRv.adapter = adapter
        adapter.setOnItemClickListener { _, _, position ->
            adapter.setCurrent(position)
            toggleDefinition()
            play(ctx, position)
        }
    }

    /**
     * 倍数播放管理
     */
    private fun initSpeed() {
        binding.playSpeed.setOnClickListener {
            mHandler.removeMessages(HIDE_CONTROL)
            mHandler.sendEmptyMessageDelayed(HIDE_CONTROL, 3000)
            toggleSpeed()
        }
        binding.playSpeedControl.speedRg.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.speed_15f -> {
                    mSpeed = 1.5f
                    mSpeedText = "1.5X"
                }
                R.id.speed_125f -> {
                    mSpeed = 1.25f
                    mSpeedText = "1.25X"
                }
                R.id.speed_1f -> {
                    mSpeed = 1.0f
                    mSpeedText = "倍数"
                }
                R.id.speed_075f -> {
                    mSpeed = 0.75f
                    mSpeedText = "0.75X"
                }
            }
            player?.setPlaybackSpeed(mSpeed)
            binding.playSpeed.text = mSpeedText
        }
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
     * @return 播放器当前是否处于全拼模式
     */
    fun isFullScreen(): Boolean {
        return !binding.playFull.isVisible
    }

    fun setFullScreen(isFull: Boolean) {
        binding.playFull.visibility = if (isFull) View.GONE else View.VISIBLE
    }
}