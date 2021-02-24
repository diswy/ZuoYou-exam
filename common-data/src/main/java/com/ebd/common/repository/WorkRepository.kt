package com.ebd.common.repository

import com.diswy.foundation.web.Resource
import com.ebd.common.config.EbdState
import com.ebd.common.vo.Homework
import com.ebd.common.vo.MyWrong
import com.ebd.common.vo.SharedWork
import com.ebd.common.vo.WrongQuestion
import com.ebd.common.web.WorkService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkRepository @Inject constructor(
    private val workService: WorkService
) {
    /**
     * 获取需要做作业列表
     * @see [EbdState]
     * @param page 页码
     * @param status 状态筛选
     */
    suspend fun getHomeworkList(page: Int, status: Int, subject: Int?, grade: Int?)
            : Resource<List<Homework>> {
        return try {
            workService.getWorkList(page, status, subject, grade).map()
        } catch (e: Exception) {
            Resource.error(e)
        }
    }

    /**
     * 获取错题本
     */
    suspend fun getWrongList(page: Int, subject: Int? = null): Resource<List<MyWrong>> {
        return try {
            workService.getWrongList(page, subject).map()
        } catch (e: Exception) {
            Resource.error(e)
        }
    }

    /**
     * 获取错题详情
     */
    suspend fun getWrongQuestion(id: Int): Resource<WrongQuestion> {
        return try {
            workService.getErrorQuestions(id).map()
        } catch (e: Exception) {
            Resource.error(e)
        }
    }

    /**
     * 老师分享出的优秀作业
     */
    suspend fun getTeacherShared(page: Int, subject: Int? = null): Resource<SharedWork> {
        return try {
            workService.getTeacherShared(page, subject).map()
        } catch (e: Exception) {
            Resource.error(e)
        }
    }
}