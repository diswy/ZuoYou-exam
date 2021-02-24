package com.zuoyouxue.ui

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.activity.viewModels
import com.diswy.foundation.base.activity.BaseBindActivity
import com.diswy.foundation.quick.onClick
import com.ebd.common.App
import com.ebd.common.viewmodel.CoreViewModel
import com.ebd.common.vo.Homework
import com.zuoyouxue.R
import com.zuoyouxue.databinding.ActivityEbdWebBinding
import com.zuoyouxue.ui.EbdWebActivity.Mode.*

/**
 * Web页，聚合
 */
class EbdWebActivity : BaseBindActivity<ActivityEbdWebBinding>() {

    /**
     * 网页启动模式。
     * @property Normal 普通模式，仅加载网页
     * @property Answer 答题预览模式，创建一个开始答题按钮
     * @property Encourage 鼓励模式，满足限定条件，奖励性弹框，类似游戏成就生涯
     */
    enum class Mode {
        Normal, Answer, Encourage
    }

    private val coreViewModel: CoreViewModel by viewModels { App.instance.factory }

    private lateinit var mUrl: String// 网页地址
    private var mMode: Mode = Normal// 启动模式,默认为普通模式
    private var mTitle: String? = null// 指定标题，如未指定，从网页中获取
    private var mHomework: Homework? = null// 答题模式，会携带作业

    override fun getLayoutRes(): Int = R.layout.activity_ebd_web

    companion object {
        private const val TITLE = "TITLE"
        private const val EXTRA_URL = "EXTRA_URL"
        private const val LAUNCH_MODE = "LAUNCH_MODE"
        private const val EXTRA_HOMEWORK = "EXTRA_HOMEWORK"

        fun start(context: Context, url: String, title: String? = null, mode: Mode = Normal) {
            val intent = Intent(context, EbdWebActivity::class.java)
            intent.putExtra(EXTRA_URL, url)
            intent.putExtra(LAUNCH_MODE, mode)
            intent.putExtra(TITLE, title)
            context.startActivity(intent)
        }

        fun start(context: Context, url: String, homework: Homework) {
            val intent = Intent(context, EbdWebActivity::class.java)
            intent.putExtra(EXTRA_URL, url)
            intent.putExtra(LAUNCH_MODE, Answer)
            intent.putExtra(EXTRA_HOMEWORK, homework)
            context.startActivity(intent)
        }
    }

    override fun setView() {
        initStatusBarColor()
        super.setView()
    }

    private fun parseIntent() {
        mUrl = intent.getStringExtra(EXTRA_URL) ?: ""
        mTitle = intent.getStringExtra(TITLE)
        mMode = intent.getSerializableExtra(LAUNCH_MODE) as Mode? ?: Normal
        mHomework = intent.getParcelableExtra(EXTRA_HOMEWORK)
    }

    private fun initView() {
        binding.btnStartAnswer.visibility = if (mHomework != null) View.VISIBLE else View.GONE
        mHomework?.let { mTitle = it.Name }
    }

    override fun initialize() {
        parseIntent()
        initView()
        lifecycle.addObserver(binding.fancyWeb)
        binding.ebdToolbar.setNavigationOnClickListener { onBackPressed() }
        binding.fancyWeb.addTitleReceiveListener { binding.ebdToolbar.title = mTitle ?: it }
        binding.fancyWeb.load(this, mUrl)
    }

    override fun bindListener() {
        binding.btnStartAnswer.onClick {
            coreViewModel.startAnswerWork(mHomework)
        }
    }

}