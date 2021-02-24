package com.zuoyouxue.ui.homework.card

import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.launcher.ARouter
import com.diswy.foundation.base.fragment.BaseBindFragment
import com.ebd.common.App
import com.ebd.common.Page
import com.ebd.common.cache.loginId
import com.ebd.common.config.DataKey
import com.ebd.common.config.Ebd
import com.ebd.common.viewmodel.CoreViewModel
import com.ebd.common.vo.Attachment
import com.ebd.common.vo.QuestionInfo
import com.ebd.common.vo.local.AnswerType
import com.ebd.common.vo.local.SAnswer
import com.ebd.common.vo.local.SingleOptions
import com.google.gson.Gson
import com.zuoyouxue.R
import com.zuoyouxue.databinding.FragmentAnswerCardBinding
import timber.log.Timber
import java.util.*

/**
 * 答题卡的父类，子类专注各自差异化处理
 */
abstract class AnswerCard : BaseBindFragment<FragmentAnswerCardBinding>() {

    lateinit var mQInfo: QuestionInfo
    /**
     * DI无法直接注入到这里，首要手动赋值
     */
    abstract fun initQInfo()

    abstract fun loadAnswer()

    abstract fun initRecycler()

    val coreViewModel: CoreViewModel by activityViewModels { App.instance.factory }
    // 获取答案,由于答案同步是异步方法，可在加载完答案后进行处理
    val mSAnswerList: ArrayList<SAnswer> by lazy { coreViewModel.getThisCardAnswer(mQInfo.Id) }
    // 获取选项.仅单选或多选题此字段才不为空
    val mOption: ArrayList<SingleOptions> by lazy { coreViewModel.getSingleOption(mQInfo.AlternativeContent) }
    // 获取答题卡
    val mCardList: ArrayList<AnswerType> by lazy { coreViewModel.getAnswerType(mQInfo.AnswerType) }
    // 试题ID
    private val mPaperId: Long by lazy { coreViewModel.getHomework().PapersId }
    // 答题卡视图表现
//    private lateinit var mCardBehavior: BottomSheetBehavior<*>

    override fun getLayoutRes(): Int = R.layout.fragment_answer_card

    override fun initialize() {
        initRecycler()
        // 测量实际高度
//        val w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
//        val h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
//        binding.cardDrawer.measure(w, h)
//        mCardBehavior = BottomSheetBehavior.from(binding.cardBehavior)
//        mCardBehavior.isHideable = false
//        mCardBehavior.peekHeight = binding.cardDrawer.measuredHeight
//        coreViewModel.getCardDrawer().observe(this, Observer { isExpand ->
//            mCardBehavior.state = if (isExpand)
//                BottomSheetBehavior.STATE_EXPANDED
//            else
//                BottomSheetBehavior.STATE_COLLAPSED
//        })
//        mCardBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
//            override fun onSlide(p0: View, p1: Float) {}
//
//            override fun onStateChanged(p0: View, p1: Int) {
//                if (p1 == BottomSheetBehavior.STATE_EXPANDED) {
//                    coreViewModel.setCardExpand(true)
//                } else if (p1 == BottomSheetBehavior.STATE_COLLAPSED) {
//                    coreViewModel.setCardExpand(false)
//                    ctx {
//                        hideSoftKeyboard(it, p0)
//                    }
//                }
//            }
//        })
//        binding.cardDrawer.setOnClickListener {
//            mCardBehavior.state = if (mCardBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
//                BottomSheetBehavior.STATE_COLLAPSED
//            } else {
//                BottomSheetBehavior.STATE_EXPANDED
//            }
//        }

        initQInfo()
        // 加载网页
        lifecycle.addObserver(binding.web)
        val url = Ebd.URL_QUESTION.format(mQInfo.Id, mPaperId, loginId)
        binding.web.load(this, url)
        Timber.d("--->>>这里执行了么？？？地址$url")
        // 监听答案
        coreViewModel.getAnswer().observe(this, Observer {
            Timber.d(Gson().toJson(it))
            // 根据答题卡类型构建学生答案
            if (mSAnswerList.isEmpty() && mCardList.isNotEmpty()) {
                for (i in 0 until mCardList.size) {
                    mSAnswerList.add(SAnswer(i + 1, mCardList[i].TypeId))
                }
            }
            Timber.d("--->>>这里执行了么？？? 加载答题卡？？？？！！！")
            loadAnswer()
        })
        // 存在附件加载附件
        showAttachment()
    }

    /**
     * 处理多附件
     */
    private fun showAttachment() {
        if (mQInfo.QuestionSubjectAttachment.isNotEmpty()) {
            val attachment = mQInfo.QuestionSubjectAttachment
            if (!(attachment.size == 1 &&
                        attachment[0].Url.toLowerCase(Locale.CHINA).endsWith(".mp3"))
            ) {// 单个视频或多附件类型
                attachment as ArrayList<Attachment>
                binding.cardAttachment.visibility = View.VISIBLE
                binding.cardAttachmentBtn.setOnClickListener {
                    ARouter.getInstance().build(Page.ATTACHMENT)
                        .withObject(DataKey.Attachment, attachment)
                        .navigation()
                }
            }
        }
    }
}