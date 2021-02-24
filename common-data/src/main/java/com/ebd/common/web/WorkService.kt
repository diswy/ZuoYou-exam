package com.ebd.common.web

import com.ebd.common.cache.loginId
import com.ebd.common.cache.loginName
import com.ebd.common.vo.*
import okhttp3.RequestBody
import retrofit2.http.*

interface WorkService {
    /**
     * 远端配置
     */
    @GET("api/Setting/GetSetting")
    suspend fun getSetting(
        @Query("keyName") keyName: String
    ): BaseResponse<String>


    /**
     * 用户登录
     */
    @GET("api/Account/Login")
    suspend fun userLogin(
        @Query("loginName") loginName: String,
        @Query("pwd") pwd: String
    ): BaseResponse<User>

    /**
     * 获取消息列表
     */
    @GET("/api/Account/GetMsgList")
    suspend fun getMsgList(
        @Query("index") index: Int,
        @Query("studentid") studentid: Int = loginId
    ): BaseResponse<Message>

    /**
     * 消息阅读反馈
     */
    @GET("/api/Account/ReadrMsg")
    suspend fun readMessage(
        @Query("type") type: Int,
        @Query("id") id: String,
        @Query("studentid") studentid: Int = loginId
    ): BaseResponse<Unit>

    /**
     * 荣誉榜
     */
    @GET("/api/Students/TeamHonors")
    suspend fun getHonor(
        @Query("studentid") studentId: Int = loginId
    ): BaseResponse<List<Honor>>

    /**
     * 个人荣誉
     */
    @GET("/api/Students/Honor")
    suspend fun getMyHonor(
        @Query("studentid") studentId: Int = loginId
    ): BaseResponse<List<HonorMe>>

    /**
     * 找回账号
     */
    @GET("api/Account/FindLoginName")
    suspend fun findUser(
        @Query("IdSerial") idCard: String
    ): BaseResponse<FindUser>

    /**
     * 获取验证码
     * @param type 0找回账号 1绑定账号
     */
    @GET("/api/Account/GetTelCode")
    suspend fun getPhoneCode(
        @Query("loginName") loginName: String,
        @Query("type") type: Int
    ): BaseResponse<Unit>

    /**
     * 绑定手机
     * @param status 0绑定手机 1修改手机
     */
    @POST("/api/Account/UpdatePhCode")
    suspend fun bindPhone(
        @Query("Status") status: Int,
        @Query("Code") code: String,
        @Query("Tel") tel: String,
        @Query("Pwd") Pwd: String,
        @Query("UserId") userId: Int = loginId
    ): BaseResponse<Unit>

    /**
     * 重置密码
     */
    @POST("api/Account/UpdatePwdCode")
    suspend fun updatePwd(
        @Query("LoginName") LoginName: String,
        @Query("NewPwd") NewPwd: String,
        @Query("Code") Code: String
    ): BaseResponse<Unit>

    /**
     * 修改密码
     */
    @POST("api/Account/EditPwd")
    suspend fun modifyPwd(
        @Query("Pwd") Pwd: String,
        @Query("NewPwd") NewPwd: String,
        @Query("UserId") UserId: Int = loginId
    ): BaseResponse<Unit>

    /**
     * 问题反馈
     */
    @POST("api/Feedback/SubmitFeedback")
    suspend fun submitFeedBk(
        @Query("Countent") content: String,
        @Query("Classify") Classify: String,
        @Query("SourceType") SourceType: String,
        @Query("WriteUserId") userId: Int = loginId,
        @Query("WriteUserName") userName: String = loginName,
        @Query("Title") title: String = "",
        @Query("Type") type: Int = 0
    ): BaseResponse<Unit>

    /**
     * 获取作业列表
     */
    @POST("api/Students/GetExaminationTasks")
    suspend fun getWorkList(
        @Query("pageindex") pageIndex: Int,
        @Query("status") status: Int?,
        @Query("SubjectTypeID") SubjectTypeID: Int? = null,
        @Query("GradeId") gradeId: Int? = null,
        @Query("pagesieze") pageSize: Int = 20,
        @Query("userid") id: Int = loginId
    ): BaseResponse<List<Homework>>

    /**
     * 获取错题本列表
     */
    @POST("api/Students/ErrorQuestionsList")
    suspend fun getWrongList(
        @Query("pageindex") pageIndex: Int,
        @Query("SubjectTypeID") SubjectTypeID: Int? = null,
        @Query("pagesieze") pageSize: Int = 20,
        @Query("userid") id: Int = loginId
    ): BaseResponse<List<MyWrong>>

    /**
     * 错题本 问题详情
     */
    @GET("api/Students/ErrorQuestions")
    suspend fun getErrorQuestions(
        @Query("StudentQuestionsTasksID") StudentQuestionsTasksID: Int
    ): BaseResponse<WrongQuestion>

    /**
     * 老师分享出的作业列表
     */
    @POST("api/TaskShare/TaskShareToStudent")
    suspend fun getTeacherShared(
        @Query("PageIndex") pageIndex: Int,
        @Query("SubjectTypeid") subject: Int?,
        @Query("Studentid") studentId: Int = loginId,
        @Query("PageSize") pageSize: Int = 20
    ): BaseResponse<SharedWork>

    /**
     * 开始答题，需调用的接口
     */
    @GET("api/Students/StartWork")
    suspend fun startAnswerWork(
        @Query("StudentQuestionsTasksID") taskId: Long
    ): BaseResponse<StartWorkInfo>

    /**
     * 获取试卷相关信息
     */
    @GET("api/Students/GetExaminationPapersByID")
    suspend fun getExamPaper(
        @Query("id") paperId: Long,
        @Query("tasksid") tasksId: Long
    ): BaseResponse<List<QuestionPaper>>

    /**
     * 提交单个题答案
     */
    @POST("api/Students/SubmitAnswer")
    suspend fun submitAnswerBySort(
        @QueryMap params: HashMap<String, String>
    ): BaseResponse<Unit>

    /**
     * 使用记录
     */
    @POST("api/Account/LoginLog")
    suspend fun userLog(@QueryMap params: HashMap<String, String>): BaseResponse<Unit>

    /**
     * 打包提交答案
     */
    @FormUrlEncoded
    @POST("api/Students/SubmitAnswerList")
    suspend fun submitAnswerPack(
        @Field("Taskid") taskId: Long,
        @Field("AnswerList") answerList: String,
        @Field("Status") Status: Int,
        @Field("Type") type: Int = 0
    ): BaseResponse<Unit>

    /**
     * 结束本次答题任务
     */
    @GET("api/Students/EndWork")
    suspend fun endWork(@Query("StudentQuestionsTasksID") taskId: Long): BaseResponse<Int>

    /**
     * 上传图片
     */
    @Multipart
    @POST("http://service.student.cqebd.cn/HomeWork/UpdataFile")
    suspend fun uploadFile(@Part("files\"; filename=\"image.jpg\"") files: RequestBody): BaseResponse<String>

    /**
     * 赶考网资源 权限验证
     */
    @GET("api/Account/Explorer")
    suspend fun getGanPer(@Query("studentid") StudentId: Int = loginId): BaseResponse<LibPermission>

    /**
     * 获取推送公告
     */
    @GET("http://ctsm-api.cqebd.cn/article/list")
    suspend fun getPushList(@Query("index") index: Int): BaseResponse<PushData>

    /**
     * 远程文档
     */
    @POST("api/Students/FileDistribution")
    suspend fun getDoc(
        @Query("PageIndex") PageIndex: Int,
        @Query("PageSize") PageSize: Int = 20,
        @Query("StudentId ") StudentId: Int = loginId
    ): BaseResponse<DocData>

}