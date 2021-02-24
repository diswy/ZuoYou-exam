package kit.diswy.player

import com.netease.neliveplayer.playerkit.sdk.VodPlayerObserver
import com.netease.neliveplayer.playerkit.sdk.model.MediaInfo
import com.netease.neliveplayer.playerkit.sdk.model.StateInfo

interface MyVodPlayerObserver : VodPlayerObserver {

    override fun onCurrentPlayProgress(
        currentPosition: Long,
        duration: Long,
        percent: Float,
        cachedPosition: Long
    ) {
    }

    override fun onSeekCompleted() {}

    override fun onCompletion() {}

    override fun onAudioVideoUnsync() {}

    override fun onNetStateBad() {}

    override fun onDecryption(ret: Int) {}

    override fun onPreparing() {}

    override fun onPrepared(info: MediaInfo) {}

    override fun onError(code: Int, extra: Int) {}

    override fun onFirstVideoRendered() {}

    override fun onFirstAudioRendered() {}

    override fun onBufferingStart() {}

    override fun onBufferingEnd() {}

    override fun onBuffering(percent: Int) {}

    override fun onVideoDecoderOpen(value: Int) {}

    override fun onStateChanged(stateInfo: StateInfo) {}

    override fun onHttpResponseInfo(code: Int, header: String) {}
}