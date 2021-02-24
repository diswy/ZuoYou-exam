package com.zuoyouxue.ui.homework.card


import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.diswy.foundation.base.Permission
import com.diswy.foundation.base.fragment.BaseBindFragment
import com.diswy.foundation.quick.filterH5
import com.diswy.foundation.quick.longToast
import com.diswy.foundation.quick.onUiThread
import com.ebd.common.App
import com.ebd.common.Page
import com.ebd.common.config.CardType
import com.ebd.common.viewmodel.CoreViewModel
import com.ebd.common.vo.QuestionInfo
import com.ebd.common.vo.local.AnswerType
import com.ebd.common.vo.local.SAnswer
import com.google.gson.Gson
import com.tencent.taisdk.TAIError
import com.tencent.taisdk.TAIOralEvaluationData
import com.tencent.taisdk.TAIOralEvaluationListener
import com.tencent.taisdk.TAIOralEvaluationRet
import com.zuoyouxue.R
import com.zuoyouxue.databinding.FragmentSoeCardBinding
import kit.diswy.tencent_soe.Soe
import org.sufficientlysecure.htmltextview.HtmlHttpImageGetter
import pub.devrel.easypermissions.AfterPermissionGranted
import timber.log.Timber


/**
 * 口语评测页面
 */
@Route(path = Page.CARD_SOE)
class SoeCard : BaseBindFragment<FragmentSoeCardBinding>(), TAIOralEvaluationListener {

    @Autowired
    @JvmField
    var qInfo: QuestionInfo? = null

    private val coreViewModel: CoreViewModel by activityViewModels { App.instance.factory }
    // 获取答案,由于答案同步是异步方法，可在加载完答案后进行处理
    private val mSAnswerList: ArrayList<SAnswer> by lazy { coreViewModel.getThisCardAnswer(qInfo!!.Id) }
    // 获取答题卡
    private val mCardList: ArrayList<AnswerType> by lazy { coreViewModel.getAnswerType(qInfo!!.AnswerType) }
    // soe
    private val soe: Soe by lazy { coreViewModel.getSoe()!! }
    private val gson: Gson = App.instance.globalGson

    override fun getLayoutRes(): Int = R.layout.fragment_soe_card

    override fun initialize() {
        lifecycle.addObserver(binding.soeLayoutScore.soeMusicPlayer)
        when (qInfo!!.QuestionTypeId) {
            CardType.EN_WORD.value -> binding.soeTitle.text = "请朗读下面单词"
            CardType.EN_SENTENCE.value -> binding.soeTitle.text = "请朗读下面例句"
            CardType.EN_PARAGRAPH.value -> binding.soeTitle.text = "请朗读下面段落"
            CardType.EN_FREE.value -> binding.soeTitle.text = "请朗读发挥随意朗读"
            CardType.EN_LISTEN_AND_READ.value -> binding.soeTitle.text = "请先聆听原声，然后朗读"
            CardType.EN_LISTEN_AND_READ_SHOW.value -> binding.soeTitle.text = "请朗读以下内容"
        }
        // Html内容解析
        binding.soeHtml.setHtml(qInfo!!.Subject, HtmlHttpImageGetter(binding.soeHtml))
        // 监听答案
        coreViewModel.getAnswer().observe(this, Observer {
            // 根据答题卡类型构建学生答案
            if (mSAnswerList.isEmpty() && mCardList.isNotEmpty()) {
                for (i in 0 until mCardList.size) {
                    mSAnswerList.add(SAnswer(i + 1, mCardList[i].TypeId))
                }
            }

            binding.soeRecord.visibility = View.VISIBLE

            // 存在答案需要将其选中
            mSAnswerList[0].let { answer ->
                if (answer.Answer.isNotBlank()) {
                    try {
                        val result = gson.fromJson(answer.Answer, TAIOralEvaluationRet::class.java)
                        showSoeResult(result)
                    } catch (e: Exception) {
                        Timber.e(e)
                    }
                }
            }
        })
    }

    /**
     * @param start true开始动画，false结束动画
     */
    @AfterPermissionGranted(Permission.RC_RECORD_PERM)
    private fun statRecord(start: Boolean) {
        if (hasRecordPermission()) {
            soe.startRecord(qInfo!!.Subject.filterH5(), qInfo!!.QuestionTypeId, this)
            if (start) animateRecordStart() else animateRecordStop()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun bindListener() {
        binding.soeRecord.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    coreViewModel.setVpScrollEnable(false)
                    statRecord(true)
                }
                MotionEvent.ACTION_UP -> {
                    statRecord(false)
                    coreViewModel.setVpScrollEnable(true)
                }
                MotionEvent.ACTION_MOVE -> {
                }
            }
            return@setOnTouchListener true
        }
    }

    override fun onVolumeChanged(volume: Int) {// 回调录音分贝大小[0-120]

    }

    override fun onEvaluationData(
        data: TAIOralEvaluationData?,
        result: TAIOralEvaluationRet?,
        error: TAIError?
    ) {// 结果回调
        if (error != null && error.code != 0) {// 录制错误
            onUiThread {
                longToast(error.desc + "请退出当前页面，重新进入")
            }
            return
        }
        if (data != null && data.bEnd && result != null) {// 录制结束
            onUiThread {
                showSoeResult(result)
            }
            mSAnswerList[0].Answer = gson.toJson(result)
            coreViewModel.putSAnswerById(qInfo!!.Id, mSAnswerList)
        }
    }

    override fun onEndOfSpeech() {// 这里可以根据业务逻辑处理，如停止录音或提示用户
    }

    /**
     * @param result 口语评测结果
     */
    private fun showSoeResult(result: TAIOralEvaluationRet) {
        binding.soeLayoutScore.root.visibility = View.VISIBLE
        binding.soeLayoutScore.soe = result
        binding.soeLayoutScore.circularProgressBar.progress = result.suggestedScore.toFloat()
        binding.soeLayoutScore.progressAccuracy.progress = result.pronAccuracy.toInt()
        binding.soeLayoutScore.progressCompletion.progress = (result.pronCompletion * 100).toInt()
        binding.soeLayoutScore.progressFluency.progress = (result.pronFluency * 100).toInt()
        binding.soeLayoutScore.soeMusicPlayer.prepare(requireActivity(), result.audioUrl)
    }

    //****************动画****************//
    private fun animateRecordStart() {
        binding.soeRecord.playAnimation()
    }

    private fun animateRecordStop() {
        binding.soeRecord.pauseAnimation()
        binding.soeRecord.frame = 0
    }
}
