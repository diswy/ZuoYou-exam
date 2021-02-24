package com.zuoyouxue.ui.homework

import android.app.AlertDialog
import android.content.Intent
import android.view.KeyEvent
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.diswy.foundation.base.activity.BaseBindActivity
import com.diswy.foundation.base.activity.BaseToolbarBindActivity
import com.diswy.foundation.base.timer.ITimer
import com.diswy.foundation.quick.toast
import com.diswy.foundation.tools.TimeKitJ
import com.diswy.foundation.view.dialog.FancyDialogFragment
import com.diswy.foundation.web.Resource
import com.diswy.foundation.web.Status
import com.ebd.common.App
import com.ebd.common.Page
import com.ebd.common.config.Ebd
import com.ebd.common.viewmodel.CoreViewModel
import com.ebd.common.vo.Attachment
import com.ebd.common.vo.Homework
import com.ebd.common.vo.QuestionGroup
import com.ebd.common.vo.QuestionInfo
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.zuoyouxue.R
import com.zuoyouxue.adapter.LightAdapter
import com.zuoyouxue.adapter.PaperAdapter
import com.zuoyouxue.databinding.ActivityAnswerBinding
import com.zuoyouxue.databinding.DialogConfirmBinding
import com.zuoyouxue.ui.EbdWebActivity
import com.zuoyouxue.ui.EndActivity
import timber.log.Timber
import java.util.*
import kotlin.system.exitProcess

@Route(path = Page.ANSWER)
class AnswerActivity : BaseBindActivity<ActivityAnswerBinding>() {

    @Autowired
    @JvmField
    var homework: Homework? = null

    @Autowired
    @JvmField
    var serviceTime: Long = 0L

    @Autowired
    @JvmField
    var qGroupList: List<QuestionGroup>? = null// 问题带分组

    private lateinit var qInfoList: List<QuestionInfo>// 铺平后的问题列表

    //    private lateinit var mBottom: BottomSheetBehavior<*>// 底部指示器
    private var limitMode = false// 是否是限时答题任务
    private val mLightAdapter = LightAdapter()// 指示器适配
    private var autoCommit: Boolean = false// 系统自动提交
    private var curPosition: Int = 0// 当前题号

    private val coreViewModel: CoreViewModel by viewModels { App.instance.factory }

    private val mLoading: FancyDialogFragment by lazy {
        FancyDialogFragment.create()
            .setLayoutRes(R.layout.dialog_loading)
            .setWidth(this, 260)
            .setCanCancelOutside(false)
    }

    private val mConfirm: FancyDialogFragment by lazy {
        FancyDialogFragment.create()
            .setLayoutRes(R.layout.dialog_confirm)
            .setWidth(resources.getDimensionPixelOffset(R.dimen.dialog_width))
            .setViewListener { dialog, dialogBind ->
                val mDialogBind: DialogConfirmBinding = dialogBind as DialogConfirmBinding
                val unDoCount = mLightAdapter.getUnDoCount()
                if (unDoCount > 0) {
                    mDialogBind.holderHintText.visibility = View.VISIBLE
                    mDialogBind.tvSubMsg.text = unDoCount.toString()
                }
                mDialogBind.dialogClose.setOnClickListener { dialog.dismiss() }
                mDialogBind.dialogConfirm.setOnClickListener {
                    dialog.dismiss()
                    autoCommit = false
                    coreViewModel.answerPackCommit(autoCommit)
                }
            }
    }

    private val mFinalCommitObserver = Observer<Resource<Boolean>> {
        when (it.status) {
            Status.LOADING -> {
                if (!mLoading.isVisible)
                    mLoading.show(supportFragmentManager, "loading")
            }
            Status.SUCCESS -> {
                mLoading.dismiss()
                if (it.data!!) {// 提交成功
                    finish()
                    startActivity(Intent(this, EndActivity::class.java))
//                    showEndDialog()
                } else {// 提交
                    toast("提交失败，请重新尝试")
                }
            }
            Status.ERROR -> {
                mLoading.dismiss()
                handleExceptions(it.throwable)
            }
        }
    }

    private val mBackSyncObserver = Observer<Resource<Boolean>> {
        when (it.status) {
            Status.LOADING -> {
                if (!mLoading.isVisible)
                    mLoading.show(supportFragmentManager, "loading")
            }
            Status.SUCCESS -> {
                mLoading.dismiss()
                super.onBackPressed()
            }
            Status.ERROR -> {
                mLoading.dismiss()
                val dialog = AlertDialog.Builder(this@AnswerActivity)
                    .setMessage(resources.getString(R.string.lostAnswer))
                    .setPositiveButton("确定") { mDialog, _ ->
                        mDialog.dismiss()
                        coreViewModel.backSync()
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
    }

    /**
     * ViewPager滑动监听
     */
    private val vpListener = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            curPosition = position
//            binding.answerBottom.answerPos.text = "%d/%d".format(position + 1, qInfoList.size)
            mLightAdapter.updateLight(position, coreViewModel.getAnswer().value)
            coreViewModel.silentSync()
            showAttachment(position)
        }
    }

//    override fun pageTitle(): String = homework!!.Name

    override fun fullScreenMode(): Boolean = true

    override fun getLayoutRes(): Int = R.layout.activity_answer

    override fun initialize() {
//        mToolbar.inflateMenu(R.menu.answer_options)
        lifecycle.addObserver(binding.answerMusicPlayer)
        coreViewModel.getFinalCommit().observe(this, mFinalCommitObserver)
        coreViewModel.getBackCommit().observe(this, mBackSyncObserver)
        // 铺平答题卡信息,初始化答题页
        qInfoList = qGroupList!!.flatMap { it.Question }
        coreViewModel.initQInfo(binding.answerVp, homework!!, qInfoList)
        // 同步答案
        coreViewModel.syncAnswer()
        // 构建答题卡
        binding.answerVp.adapter = PaperAdapter(this, qInfoList)
        binding.answerVp.registerOnPageChangeCallback(vpListener)
        binding.answerVp.isUserInputEnabled = false// vp是否可人为滑动
        binding.answerVp.offscreenPageLimit = 1
        // 是否有口语评测题型
        val soeList = qInfoList.filter { it.QuestionTypeId in 32..37 }
        Timber.d("存在口语评测题目数量 = ${soeList.size}")
        if (soeList.isNotEmpty()) {
            coreViewModel.initSoe(this)
            lifecycle.addObserver(coreViewModel.getSoe()!!)
        }
        // 视图
//        mBottom = BottomSheetBehavior.from(binding.answerLight.root)
//        mBottom.isHideable = true
//        mBottom.state = BottomSheetBehavior.STATE_HIDDEN
        Timber.d("答题时间：${homework!!.StartTime},限制时间：${homework!!.Duration}")
        if (homework!!.Duration == 0) {// 练习模式，无时间限制
            val endTime = TimeKitJ.formatTime(homework!!.CanEndDateTime)
            limitMode = false
            val oneDay: Long = 24 * 60 * 60 * 1000
            if (serviceTime + oneDay < endTime) {// 距离交卷时间还有1天以上
                binding.answerTime.text = "闯关模式"
            } else {
                val limitTime = TimeKitJ.getDurationByEnd(homework!!.CanEndDateTime, serviceTime)
                downCountTime(limitTime)
            }
        } else {// 限时模式，时间结束后需要提交
            limitMode = true
            startLimitTime()
        }
//        binding.answerBottom.answerPos.text = "1/%d".format(qInfoList.size)
        binding.answerLight.signRv.adapter = mLightAdapter
        mLightAdapter.setNewData(qInfoList as MutableList<QuestionInfo>)
        coreViewModel.getAnswer().observe(this, Observer {
            mLightAdapter.updateLight(0, coreViewModel.getAnswer().value)
        })
        mLightAdapter.setOnItemClickListener { _, _, pos ->
            binding.answerVp.currentItem = pos
        }
        // 处理首道题
        showAttachment(0)
    }

    override fun bindListener() {
//        mToolbar.setOnMenuItemClickListener {
//            if (it.itemId == R.id.answer_draft) {
//                startActivity(Intent(this, DraftActivity::class.java))
//            } else if (it.itemId == R.id.answer_commit) {
//                if (!mConfirm.isVisible) {
//                    mConfirm.show(supportFragmentManager, "confirm_again")
//                }
//            }
//            return@setOnMenuItemClickListener true
//        }
//        binding.answerBottom.answerCommit.setOnClickListener {
//            if (!mConfirm.isVisible) {
//                mConfirm.show(supportFragmentManager, "confirm_again")
//            }
//        }

//        binding.answerBottom.answerIndicator.setOnClickListener {
//            mBottom.state = if (mBottom.state == BottomSheetBehavior.STATE_HIDDEN)
//                BottomSheetBehavior.STATE_EXPANDED
//            else
//                BottomSheetBehavior.STATE_HIDDEN
//        }

        binding.answerLight.answerLast.setOnClickListener {
            if (curPosition != 0) {
                --curPosition
                binding.answerVp.setCurrentItem(curPosition, true)
            } else {
                toast("当前是第一关~")
            }
        }

        binding.answerLight.answerNext.setOnClickListener {
            if ((curPosition + 1) < qInfoList.size) {
                ++curPosition
                binding.answerVp.setCurrentItem(curPosition, true)
            } else {
                toast("已经是最后一关了~")
            }
        }

        binding.answerCommit.setOnClickListener {
            if (!mConfirm.isVisible) {
                mConfirm.show(supportFragmentManager, "confirm_again")
            }
        }
    }

    override fun onBackPressed() {
        if (limitMode) {
            toast("限时闯关不能退出，请先结束闯关！")
        } else {
            coreViewModel.backSync()
        }
    }

    private fun showEndDialog() {
        val msg = if (autoCommit) {
            "时间结束，系统已自动为你结束，是否要查看本次做题记录？"
        } else {
            "闯关已结束，是否要查看本次记录？"
        }
        val dialog = AlertDialog.Builder(this).setMessage(msg)
            .setPositiveButton("查看记录") { dialog, _ ->
                val url = Ebd.URL_END_WORK.format(homework!!.TaskId)
                startActivity(Intent(this, MyWorkActivity::class.java))
                EbdWebActivity.start(this, url, homework!!.Name)
                dialog.dismiss()
            }
            .setNegativeButton("不想看") { dialog, _ ->
                startActivity(Intent(this, MyWorkActivity::class.java))
                dialog.dismiss()
            }
            .create()
        dialog.setCancelable(false)
        dialog.show()
    }

    //****************视图管理****************//
    /**
     * 显示附件
     * @pos 题目标号
     */
    private fun showAttachment(pos: Int) {
        if (qInfoList[pos].QuestionSubjectAttachment.isNotEmpty()) {
            val attachment = qInfoList[pos].QuestionSubjectAttachment// 单道题附件
            if (attachment.size == 1 &&
                attachment[0].Url.toLowerCase(Locale.CHINA).endsWith(".mp3")
            ) {// 通常为英语听力题，仅有1个mp3
                binding.answerMusicPlayer.visibility = View.VISIBLE
                prepareMp3(attachment[0])
            }
        } else {
            lastMediaUrl = ""
            binding.answerMusicPlayer.stop()
            binding.answerMusicPlayer.visibility = View.GONE
        }
    }

    private var lastMediaUrl = ""// 上一次媒体地址，判断是否需要重新加载

    /**
     * @param data 音频附件
     */
    private fun prepareMp3(data: Attachment) {
        if (lastMediaUrl != data.Url) {// // 中断播放
            binding.answerMusicPlayer.prepare(this, data.Url)
            lastMediaUrl = data.Url
        }
    }

    /**
     * 限时答题计时
     */
    private fun startLimitTime() {
        val startTime = if (homework!!.StartTime.isNullOrBlank())
            TimeKitJ.TFULL(System.currentTimeMillis())
        else
            homework!!.StartTime

        val limitTime: Long = TimeKitJ.getDurationByStart(startTime, homework!!.Duration)
        downCountTime(limitTime)
    }

    /**
     * 限时
     */
    private fun downCountTime(time: Long) {
        timer(time, object : ITimer {
            override fun onTime(second: Int) {
                val mMinute: Int = second / 60
                val mSecond: Int = second % 60
                binding.answerTime.text =
                    "%d%d:%d%d".format(mMinute / 10, mMinute % 10, mSecond / 10, mSecond % 10)

                if (second == 180) {// 剩余3分钟提示
                    AlertDialog.Builder(this@AnswerActivity).setMessage("闯关还有3分钟，请抓紧时间！")
                        .setPositiveButton("确定") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .create()
                        .show()
                }
            }

            override fun onTimeEnd() {
                binding.answerTime.text = "时间结束"
                autoCommit = true
                coreViewModel.answerPackCommit(autoCommit)
            }
        })
    }


    private var exitTime: Long = 0
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                toast("再按一次即将退出应用")
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
