package com.ebd.common.repository

import android.content.Context
import com.diswy.foundation.throwable.UploadException
import com.diswy.foundation.web.Resource
import com.ebd.common.App
import com.ebd.common.vo.QuestionGroup
import com.ebd.common.web.WorkService
import okhttp3.MediaType
import okhttp3.RequestBody
import top.zibin.luban.Luban
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CoreRepository @Inject constructor(
    private val workService: WorkService
) {
    /**
     * [startAnswerWork]获取提交模式
     */
    private var commitMode: Int = 0// 提交模式

    private var startTime: String = ""// 开始答题时间

    private var nowTime: Long = 0L// 服务器时间

    fun getStartTime() = startTime

    fun getServiceTime() = nowTime

    /**
     * 开始答题,确定提交模式。
     * 取出答题卡
     */
    suspend fun startAnswerWork(taskId: Long, paperId: Long): List<QuestionGroup>? {
        return try {
            val startInfo = workService.startAnswerWork(taskId).data
            commitMode = startInfo?.submitMode ?: 0
            startTime = startInfo?.startDate ?: ""
            nowTime = startInfo?.nowDate ?: 0L
            workService.getExamPaper(paperId, taskId).data?.get(0)?.QuestionGruop
        } catch (e: Exception) {
            App.instance.handleExceptions(e)
            null
        }
    }

    /**
     * 提交单个题
     * @return 是否提交成功
     */
    suspend fun submitAnswerById(params: HashMap<String, String>): Boolean {
        return try {
            workService.submitAnswerBySort(params).isSuccess
        } catch (e: Exception) {
            App.instance.handleExceptions(e)
            false
        }
    }

    /**
     * 打包提交答案[commitMode]
     * @return 是否提交成功
     */
    suspend fun submitAnswerPack(taskId: Long, answerPack: String): Boolean {
        return try {
            val data = workService.submitAnswerPack(taskId, answerPack, commitMode)
            if (data.isSuccess) {
                workService.endWork(taskId).isSuccess
            } else {
                false
            }
        } catch (e: Exception) {
            App.instance.handleExceptions(e)
            false
        }
    }

    /**
     * 上传图片
     * @return 图片地址
     */
    suspend fun uploadImg(context: Context, imageFile: File): Resource<String> {
        return try {
            // 压缩图片
            val zipImage = Luban.with(context).load(imageFile).get()[0]
            val requestBody = RequestBody.create(MediaType.parse("image/jpeg"), zipImage)
            // 上传图片
            val result = workService.uploadFile(requestBody)
            if (result.isSuccess) {
                Resource.success(result.data)
            } else {
                Resource.error(UploadException(result.message))
            }
        } catch (e: Exception) {
            Resource.error(e)
        }
    }
}