package com.zuoyouxue.ui.video

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
import android.view.WindowManager
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.diswy.foundation.base.activity.BaseBindActivity
import com.diswy.foundation.quick.toast
import com.diswy.foundation.tools.IntervalKit
import com.diswy.foundation.view.dialog.FancyDialogFragment
import com.diswy.foundation.web.Resource
import com.diswy.foundation.web.Status
import com.ebd.common.App
import com.ebd.common.Page
import com.ebd.common.cache.CacheTool
import com.ebd.common.cache.loginId
import com.ebd.common.config.DataKey
import com.ebd.common.config.EbdState
import com.ebd.common.viewmodel.VideoViewModel
import com.ebd.common.vo.Period
import com.ebd.common.vo.PeriodInfo
import com.zuoyouxue.R
import com.zuoyouxue.adapter.VodAdapter
import com.zuoyouxue.databinding.ActivityVodBinding
import com.zuoyouxue.tool.filterVodUrl
import kit.diswy.player.MyPlayerStatusObserver
import kit.diswy.player.PlayerEvent
import timber.log.Timber

/**
 * 点播视频
 */
@Route(path = Page.VIDEO_VOD)
class VodActivity : BaseBindActivity<ActivityVodBinding>(), PlayerEvent, MyPlayerStatusObserver {

    @Autowired
    @JvmField
    var periodList: MutableList<Period>? = null

    @Autowired
    @JvmField
    var position: Int = -1

    private val adapter = VodAdapter()

    private var lastPeriodId = -1// 切换视频之前的ID

    private val videoViewModel: VideoViewModel by viewModels { App.instance.factory }

    private val mLoading: FancyDialogFragment by lazy {
        FancyDialogFragment.create()
            .setLayoutRes(R.layout.dialog_loading)
            .setWidth(this, 260)
            .setCanCancelOutside(false)
    }

    private lateinit var intervalKit: IntervalKit// 计时器

    private val infoObserver = Observer<Resource<PeriodInfo>> {
        when (it.status) {
            Status.LOADING -> {
            }
            Status.SUCCESS -> {
                it.data?.let { myData ->
                    binding.vodPlayer.play(this, filterVodUrl(myData.VodPlayList))
                }
            }
            Status.ERROR -> handleExceptions(it.throwable)
        }
    }

    private val recordObserver = Observer<Resource<Boolean>> {
        when (it.status) {
            Status.LOADING -> {
                if (!mLoading.isVisible)
                    mLoading.show(supportFragmentManager, "loading")
            }
            Status.SUCCESS -> {
                mLoading.dismiss()
                if (it.data == true) {// 提交成功
                    super.onBackPressed()
                } else {// 提交
                    mLoading.dismiss()
                    val dialog = AlertDialog.Builder(this)
                        .setMessage(resources.getString(R.string.lostRecord))
                        .setPositiveButton("确定") { mDialog, _ ->
                            mDialog.dismiss()
                            videoViewModel.playRecord(
                                lastPeriodId,
                                intervalKit.getDuration(),
                                false
                            )
                        }
                        .setNegativeButton("取消") { mDialog, _ ->
                            mDialog.dismiss()
                            super.onBackPressed()
                        }
                        .create()
                    dialog.setCanceledOnTouchOutside(false)
                    dialog.show()
                }
            }
            Status.ERROR -> handleExceptions(it.throwable)
        }
    }


    override fun keepScreenOn(): Boolean = true

    override fun emptyBackground(): Boolean = true

    override fun setView() {
        window.statusBarColor = Color.BLACK
        super.setView()
    }

    override fun getLayoutRes(): Int = R.layout.activity_vod

    override fun initialize() {
        intervalKit = IntervalKit()
        setCurPeriod(periodList!![position].Id)
        // 绑定生命周期
        lifecycle.addObserver(binding.vodPlayer)
        lifecycle.addObserver(intervalKit)

        binding.vodPlayer.addEventListener(this)
        binding.vodPlayer.addStatusListener(this)
        videoViewModel.periodInfo.observe(this, infoObserver)
        videoViewModel.vodRecord.observe(this, recordObserver)

        binding.vodListRv.adapter = adapter
        binding.vodListRv.addItemDecoration(myDivider(R.color.line))
        adapter.setNewData(periodList)

        Timber.d(periodList.toString())
        adapter.selected(position)
        videoViewModel.getPeriodInfo(periodList!![position].Id)
    }

    override fun bindListener() {
        adapter.setOnItemClickListener { _, _, pos ->
            val period = adapter.data[pos]
            when (period.Status) {
                EbdState.VIDEO_VOD -> {
                    setCurPeriod(period.Id)
                    adapter.selected(pos)
                    videoViewModel.getPeriodInfo(period.Id)
                }
                EbdState.VIDEO_LIVE -> {
                    ARouter.getInstance().build(Page.VIDEO_OBS_LIVE)
                        .withInt(DataKey.PeriodId, period.Id)
                        .navigation()
                }
                else -> {
                    toast("视频未准备好，还不能播放")
                }
            }
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

    /**
     * 播放状态
     */
    override fun onPlayerStart() {
        intervalKit.start()
        videoViewModel.playRecord(lastPeriodId, intervalKit.getDuration())
    }

    override fun onPlayerPause() {
        intervalKit.stop()
        videoViewModel.playRecord(lastPeriodId, intervalKit.getDuration())
    }

    override fun onPlayerNetBad() {
        toast("当前网速较差，请更换网络或尝试切换分辨率。")
    }

    override fun onPlayerCompletion() {
        intervalKit.stop()
        videoViewModel.playRecord(lastPeriodId, intervalKit.getDuration())
    }

    override fun onBackPressed() {
        if (binding.vodPlayer.isFullScreen()) {
            binding.vodPlayer.setFullScreen(false)
            toggleScreen(false)
        } else {
            videoViewModel.playRecord(lastPeriodId, intervalKit.getDuration(), false)
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private fun toggleScreen(isFull: Boolean) {
        val lp = binding.vodPlayer.layoutParams as LinearLayout.LayoutParams
        if (isFull) {
            lp.height = LinearLayout.LayoutParams.MATCH_PARENT
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )

        } else {
            lp.height = resources.getDimensionPixelOffset(R.dimen.player_height)
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
        binding.vodPlayer.layoutParams = lp
    }

    private fun setCurPeriod(curPeriodId: Int) {
        if (lastPeriodId != -1) {// 先上报记录
            intervalKit.stop()
            videoViewModel.playRecord(lastPeriodId, intervalKit.getDuration())
        }
        lastPeriodId = curPeriodId
        val localRecordTime = CacheTool.getValue("vod_${loginId}_${curPeriodId}", 1)
        intervalKit.setCurrentDuration(localRecordTime)
    }
}
