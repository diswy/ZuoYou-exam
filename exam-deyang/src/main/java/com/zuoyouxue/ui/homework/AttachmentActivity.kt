package com.zuoyouxue.ui.homework

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.text.Html
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.diswy.foundation.base.activity.BaseBindActivity
import com.diswy.foundation.quick.toast
import com.ebd.common.Page
import com.ebd.common.cache.CacheTool
import com.ebd.common.cache.loginId
import com.ebd.common.config.Ebd
import com.ebd.common.vo.Attachment
import com.ebd.common.vo.Homework
import com.zuoyouxue.R
import com.zuoyouxue.adapter.AttachmentAdapter
import com.zuoyouxue.databinding.ActivityAttachmentBinding
import com.zuoyouxue.ui.EbdWebActivity
import kit.diswy.player.MyPlayerStatusObserver
import kit.diswy.player.PlayerEvent
import timber.log.Timber

/**
 * 附件播放页，两种模式
 * 1.Homework不为空，先观看视频，后作答
 * 2.Attachment不为空，题目中附件
 */
@Route(path = Page.ATTACHMENT)
class AttachmentActivity : BaseBindActivity<ActivityAttachmentBinding>(), PlayerEvent {

    @Autowired
    @JvmField
    var homework: Homework? = null

    @Autowired
    @JvmField
    var attachment: List<Attachment>? = null

    private val adapter = AttachmentAdapter()

    override fun keepScreenOn(): Boolean = true

    override fun getLayoutRes(): Int = R.layout.activity_attachment

    override fun setView() {
        window.statusBarColor = Color.BLACK
        super.setView()
    }

    override fun initialize() {
        lifecycle.addObserver(binding.attachmentPlayer)
        binding.attachmentPlayer.addEventListener(this)

        binding.attachmentRv.adapter = adapter

        Timber.d(homework.toString())

        // 模式1
        homework?.let { work ->
            btnEnabled()// 检查附件是否已经观看
            adapter.setNewData(work.attachments as MutableList<Attachment>)
            modifyRecord(work.attachments!![0])
            val note: String = work.attachments!![0].Remarks ?: ""
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
                binding.attachmentHtml.text = Html.fromHtml(note, Html.FROM_HTML_MODE_COMPACT)
            } else {
                binding.attachmentHtml.text = Html.fromHtml(note)
            }

            binding.attachmentBtn.visibility = View.VISIBLE
            binding.attachmentBtn.setOnClickListener {
                // 新作业、作答中
                val url = Ebd.URL_START_WORK.format(work.PapersId, work.TaskId)
                EbdWebActivity.start(this, url, work)
            }
        }

        // 模式2
        attachment?.let { list ->
            list as MutableList
            adapter.setNewData(list)
            binding.attachmentPlayer.play(this, list[0].Url)
        }

    }

    override fun bindListener() {
        adapter.setOnItemClickListener { _, _, position ->
            val item = adapter.getItem(position)
            modifyRecord(item)
        }
    }

    override fun isFull(isFull: Boolean) {
        toggleScreen(isFull)
    }

    override fun playerBack() {
        onBackPressed()
    }

    override fun onBackPressed() {
        if (binding.attachmentPlayer.isFullScreen()) {
            binding.attachmentPlayer.setFullScreen(false)
            toggleScreen(false)
        } else {
            super.onBackPressed()
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private fun toggleScreen(isFull: Boolean) {
        val lp = binding.attachmentPlayer.layoutParams as LinearLayout.LayoutParams
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
        binding.attachmentPlayer.layoutParams = lp
    }

    /**
     * 开始答题按钮是否可用
     */
    private fun btnEnabled() {
        homework?.attachments?.forEach { attachment ->
            // 学生ID+附件ID
            val watchCache = CacheTool.getValue("${loginId}_${attachment.Id}", 0)
            // 该附件一次没有观看过
            if (attachment.AnswerType == 2 && watchCache == 0) {
                binding.attachmentBtn.isEnabled = false
                binding.attachmentBtn.text = "先观看视频"
                return
            }
        }
        binding.attachmentBtn.isEnabled = true
        binding.attachmentBtn.text = "开始答题"
    }

    /**
     * 验证播放记录
     */
    private fun modifyRecord(attachment: Attachment) {
        val mRecord = CacheTool.getValue("${loginId}_${attachment.Id}_record", 0L)
        if (mRecord > 0) {
            binding.attachmentPlayer.showPlayRecord(mRecord)
        }
        binding.attachmentPlayer.play(this, attachment.Url)
        if (attachment.AnswerType == 2) {// 该模式禁用进度条
            // 学生ID+附件ID
            val watchCache = CacheTool.getValue("${loginId}_${attachment.Id}", 0)
            if (watchCache == 0) {// 一次也没看过需要禁用拖动条
                binding.attachmentPlayer.disableSeek()
            } else {
                binding.attachmentPlayer.enableSeek()
            }
        }
        binding.attachmentPlayer.addRecordListener {
            if (it > mRecord) {
                CacheTool.setValue("${loginId}_${attachment.Id}_record" to it)
            }
        }
        binding.attachmentPlayer.addStatusListener(object : MyPlayerStatusObserver {
            override fun onPlayerStart() {
            }

            override fun onPlayerPause() {
            }

            override fun onPlayerNetBad() {
                toast("当前网络差，请检查网络，或更换网络")
            }

            override fun onPlayerCompletion() {
                val watchCache = CacheTool.getValue("${loginId}_${attachment.Id}", 0)
                CacheTool.setValue("${loginId}_${attachment.Id}" to (watchCache + 1))
                btnEnabled()
            }

        })
    }

}
