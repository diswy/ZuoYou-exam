package com.ebd.common.repository

import android.os.Build
import com.diswy.foundation.web.Resource
import com.ebd.common.AppInfo
import com.ebd.common.vo.*
import com.ebd.common.web.VideoService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VideoRepository @Inject constructor(
    private val videoService: VideoService,
    private val appInfo: AppInfo
) {
    /**
     * 获取需要做作业列表
     */
    suspend fun getVideoList(subjectId: Int?, gradeId: Int?): Resource<List<Video>> {
        return try {
            videoService.getVideoList(subjectId, gradeId).map()
        } catch (e: Exception) {
            Resource.error(e)
        }
    }

    /**
     * 获取[Video]中的[Period]
     * @param videoId 视频ID
     */
    suspend fun getVideoPeriod(videoId: Int): Resource<List<Period>> {
        return try {
            videoService.getVideoPeriod(videoId).map()
        } catch (e: Exception) {
            Resource.error(e)
        }
    }

    /**
     * 根据时间获取课程表
     * @param month 月份
     */
    suspend fun getVideoPeriodByMonth(month: String): Resource<List<Period>> {
        return try {
            videoService.getVideoPeriodByMonth(month).map()
        } catch (e: Exception) {
            Resource.error(e)
        }
    }

    /**
     * 获取课时详情[PeriodInfo],其中[VodPlay]为播放地址
     * @param periodId 课时ID
     */
    suspend fun getPeriodInfo(periodId: Int): Resource<PeriodInfo> {
        return try {
            videoService.getPeriodInfo(periodId).map()
        } catch (e: Exception) {
            Resource.error(e)
        }
    }

    /**
     * 获取直播详情
     * @param periodId 课时ID
     */
    suspend fun getLiveInfo(periodId: Int): Resource<LiveInfo> {
        return try {
            videoService.getLiveInfo(periodId).map()
        } catch (e: Exception) {
            Resource.error(e)
        }
    }

    /**
     * 课时播放记录
     * @param periodId 课时ID
     * @param duration 时长
     */
    suspend fun playRecord(periodId: Int, duration: Int): Resource<Boolean> {
        return try {
            val deviceInfo = "${Build.MODEL}|${appInfo.versionName()}"
            Resource.success(videoService.playRecord(periodId, duration, deviceInfo).isSuccess)
        } catch (e: Exception) {
            Resource.error(e)
        }
    }
}