package kit.diswy.player

import com.netease.neliveplayer.playerkit.sdk.LivePlayerObserver
import com.netease.neliveplayer.playerkit.sdk.model.MediaInfo
import com.netease.neliveplayer.playerkit.sdk.model.StateInfo

interface MyLivePlayerObserver : LivePlayerObserver {
    override fun onPreparing() {
    }

    override fun onPrepared(mediaInfo: MediaInfo?) {
    }

    override fun onError(code: Int, extra: Int) {
    }

    override fun onFirstVideoRendered() {
    }

    override fun onFirstAudioRendered() {
    }

    override fun onBufferingStart() {
    }

    override fun onBufferingEnd() {
    }

    override fun onBuffering(percent: Int) {
    }

    override fun onVideoDecoderOpen(value: Int) {
    }

    override fun onStateChanged(stateInfo: StateInfo?) {
    }

    override fun onHttpResponseInfo(code: Int, header: String?) {
    }

}