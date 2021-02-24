package kit.diswy.tencent_soe

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.google.gson.Gson
import com.tencent.taisdk.*
import timber.log.Timber
import java.util.*

/**
 * @param coeff 苛刻指数，取值为[1.0 - 4.0]范围内的浮点数，用于平滑不同年龄段的分数，1.0为小年龄段，4.0为最高年龄段
 */
class Soe(private val ctx: Context, private val coeff: Double) : LifecycleObserver {

    private val oral = TAIOralEvaluation()

    init {
        val recordParam = TAIRecorderParam()
        recordParam.fragSize = 1024
        recordParam.fragEnable = true// 分片
        recordParam.vadEnable = true// 静音检测
        recordParam.vadInterval = 5000
        oral.setRecorderParam(recordParam)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun lifeOnDestroy() {
        oral.setListener(null)
        oral.stopRecordAndEvaluation { }
    }

    /**
     * 开始录音
     * @param content 评测内容
     * @param mode 评测类型
     */
    fun startRecord(content: String, mode: Int, listener: TAIOralEvaluationListener) {
        if (oral.isRecording) {// 录音中，关闭
            oral.stopRecordAndEvaluation {
                Timber.d("主动关闭->：${Gson().toJson(it)}")
            }
        } else {
            oral.setListener(listener)
            oral.startRecordAndEvaluation(buildTAIParam(content, mode)) {
                Timber.d("主动开始录制：${it.code},${it.desc},${it.requestId}")
            }
        }
    }

    /**
     * @param content 评测内容
     * @param mode 评测类型
     * @return 评测参数，内容发生改变时需要重新构建
     */
    private fun buildTAIParam(content: String, mode: Int): TAIOralEvaluationParam {
        val taiParam = TAIOralEvaluationParam()
        taiParam.context = ctx
        taiParam.sessionId = UUID.randomUUID().toString()
        taiParam.appId = SoeConfig.appId
        taiParam.secretId = SoeConfig.secretId
        taiParam.secretKey = SoeConfig.secretKey
        taiParam.soeAppId = SoeConfig.soeAppId
        taiParam.storageMode = TAIOralEvaluationStorageMode.ENABLE// 是否存储音频文件在服务器
        taiParam.fileType = TAIOralEvaluationFileType.MP3// 目前仅支持MP3
        taiParam.serverType = TAIOralEvaluationServerType.ENGLISH// 评测语言
        taiParam.textMode = TAIOralEvaluationTextMode.NORMAL
        taiParam.scoreCoeff = coeff

        when (mode) {
            SoeConfig.word -> {
                taiParam.refText = content
                taiParam.workMode = TAIOralEvaluationWorkMode.STREAM
                taiParam.evalMode = TAIOralEvaluationEvalMode.WORD
            }
            SoeConfig.sentence -> {
                taiParam.refText = content
                taiParam.workMode = TAIOralEvaluationWorkMode.STREAM
                taiParam.evalMode = TAIOralEvaluationEvalMode.SENTENCE
            }
            SoeConfig.paragraph -> {
                taiParam.refText = content
                taiParam.workMode = TAIOralEvaluationWorkMode.STREAM
                taiParam.evalMode = TAIOralEvaluationEvalMode.PARAGRAPH
            }
            SoeConfig.free -> {
                taiParam.workMode = TAIOralEvaluationWorkMode.STREAM
                taiParam.evalMode = TAIOralEvaluationEvalMode.FREE
            }
            SoeConfig.listenRead -> {// 跟读，目前选最长的
                taiParam.workMode = TAIOralEvaluationWorkMode.STREAM
                taiParam.evalMode = TAIOralEvaluationEvalMode.PARAGRAPH
                taiParam.refText = content
            }
        }
        return taiParam
    }
}