package kit.diswy.player

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.SeekBar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.diswy.kit.R
import com.diswy.kit.databinding.MusicPlayerViewBinding
import com.netease.neliveplayer.playerkit.sdk.PlayerManager
import com.netease.neliveplayer.playerkit.sdk.VodPlayer
import com.netease.neliveplayer.playerkit.sdk.model.SDKOptions
import com.netease.neliveplayer.playerkit.sdk.model.VideoBufferStrategy
import com.netease.neliveplayer.playerkit.sdk.model.VideoOptions
import com.netease.neliveplayer.proxy.config.NEPlayerConfig
import timber.log.Timber
import java.util.*

class MusicPlayer @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), LifecycleObserver {
    companion object {
        const val SHOW_PROGRESS = 0x01
    }

    // UI
    private val binding: MusicPlayerViewBinding =
        DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.music_player_view,
            this,
            true
        )

    private var player: VodPlayer? = null
    private var totalDuration: Long = 0
    private val options: VideoOptions = VideoOptions().apply {
        hardwareDecode = true
        bufferStrategy = VideoBufferStrategy.ANTI_JITTER
    }

    private val mHandler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                SHOW_PROGRESS -> {
                    val position = setProgress()
                    sendMessageDelayed(obtainMessage(SHOW_PROGRESS), 1000 - position % 1000)
                }
            }
        }
    }

    private val playerObserver = object : MyVodPlayerObserver {
        override fun onFirstAudioRendered() {
            binding.musicSeek.progress = 0
            mHandler.sendEmptyMessage(SHOW_PROGRESS)
        }

        override fun onCompletion() {
            mHandler.removeCallbacksAndMessages(null)
            reloadView(false)
            binding.musicSeek.progress = 100
        }
    }

    private val mProgressSeekListener = object : SeekBar.OnSeekBarChangeListener {

        override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
            val mCurrentTime = stringForTime(totalDuration * i / 100)
            val mEndTime = stringForTime(totalDuration)
            binding.musicTime.text = "%s/%s".format(mCurrentTime, mEndTime)
        }

        override fun onStartTrackingTouch(seekBar: SeekBar) {
            mHandler.removeMessages(SHOW_PROGRESS)
        }

        override fun onStopTrackingTouch(seekBar: SeekBar) {
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

        binding.musicPlayPause.setOnClickListener {
            if (isPlaying()) {// 播放中 点击暂停
                pause()
            } else {// 继续播放
                start()
            }
        }

        binding.musicSeek.setOnSeekBarChangeListener(mProgressSeekListener)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private fun lifeOnResume() {
//        player?.onActivityResume(false)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    private fun lifeOnPause() {
        pause()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private fun lifeOnStop() {
        player?.onActivityStop(false)
    }

    /**
     * 准备音频地址
     */
    fun prepare(ctx: Context, path: String) {
        releasePlayer()// 释放，保证只有一个实例在播放
        player = PlayerManager.buildVodPlayer(ctx, path, options)
        player?.registerPlayerObserver(playerObserver, true)
    }

    /**
     * 音频播放
     */
    fun start() {
        player?.start()
        reloadView(true)
    }

    /**
     * 音频暂停
     */
    fun pause() {
        player?.pause()
        reloadView(false)
    }

    /**
     * 音频停止
     */
    fun stop() {
        releasePlayer()
    }

    /**
     * 释放播放器
     */
    private fun releasePlayer() {
        reloadView(false)
        mHandler.removeCallbacksAndMessages(null)
        binding.musicTime.text = ""
        binding.musicSeek.progress = 0
        player?.apply {
            registerPlayerObserver(playerObserver, false)
            stop()
        }
        player = null
    }

    /**
     * 播放器状态
     */
    private fun isPlaying(): Boolean {
        return player?.isPlaying ?: false
    }

    /**
     * 改变按钮
     * @param state true开始播放 false停止播放
     */
    private fun reloadView(state: Boolean) {
        if (state) {
            binding.musicPlayPause.setImageResource(R.drawable.ic_music_pause)
            binding.musicWave.resumeAnimation()
        } else {
            binding.musicPlayPause.setImageResource(R.drawable.ic_music_play)
            binding.musicWave.pauseAnimation()
        }
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
        Timber.d("当前：%d ; 总长度：%d".format(position, duration))
        if (duration > 0) {
            val pos = 100L * position / duration
            binding.musicSeek.progress = pos.toInt()
            mEndTime = stringForTime(duration)
        }
        val mCurrentTime = stringForTime(position)
        binding.musicTime.text = "%s/%s".format(mCurrentTime, mEndTime)
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
}