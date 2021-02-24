package com.ebd.common.web

import com.ebd.common.cache.loginId
import com.ebd.common.vo.*
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface VideoService {

    /**
     * 更新用户头像
     */
    @GET("api/Account/UpdateStudentPhoto")
    suspend fun updateAvatar(
        @Query("photo") imgUrl: String,
        @Query("id") studentId: Int = loginId
    ): BaseResponse<Unit>

    /**
     * [Video]中可包含数个[Period]
     */
    @POST("api/CoursePeriod/GetCourseList")
    suspend fun getVideoList(
        @Query("SubjectTypeId") subjectType: Int? = null,
        @Query("GradeId") gradeId: Int? = null,
        @Query("Type") type: Int = 2,
        @Query("studentid") userId: Int = loginId
    ): BaseResponse<List<Video>>

    /**
     * 获取课时
     */
    @POST("api/CoursePeriod/GetPeriodList")
    suspend fun getVideoPeriod(
        @Query("CourseId") id: Int,
        @Query("studentid") userId: Int = loginId
    ): BaseResponse<List<Period>>

    /**
     * 获取课程表
     */
    @POST("api/CoursePeriod/GetPeriodList")
    suspend fun getVideoPeriodByMonth(
        @Query("Month") date: String,
        @Query("studentid") userId: Int = loginId
    ): BaseResponse<List<Period>>

    /**
     * 获取课时播放地址
     */
    @GET("api/CoursePeriod/GetPeriodByID")
    suspend fun getPeriodInfo(
        @Query("id") id: Int,
        @Query("studentid") userId: Int = loginId
    ): BaseResponse<PeriodInfo>

    /**
     * 获取直播
     */
    @GET("/api/PeriodLive/GetPeriodLiveByPerodId")
    suspend fun getLiveInfo(
        @Query("periodId") id: Int
    ): BaseResponse<LiveInfo>

    /**
     * 课时播放记录
     */
    @POST("api/PeriodReview/Add")
    suspend fun playRecord(
        @Query("PeriodId") PeriodId: Int,
        @Query("Duration") Duration: Int,
        @Query("SourceName") SourceName: String,
        @Query("SourceType") SourceType: Int = 1,// 1 点播  2直播
        @Query("StudentId") StudentId: Int = loginId
    ): BaseResponse<Unit>
}