package com.ebd.common.config

object Ebd {
    // 填空题作答，延时处理答案时间
    const val EDITOR_DELAY: Long = 600
    // 轻量储存文件名 可被共享
    const val FILE_NAME = "zyx_preferences"
    // 点点作业域名
    const val BASE_WORK = "http://service.ex.cqebd.cn/"
    // 点点视频域名
    const val BASE_VIDEO = "http://service.onlin.cqebd.cn/"
    // 点点作业网关
    const val WORK_GATE_KEY = "23393048"
    const val WORK_GATE_SECRET = "d0c983467d8ced6568e844c0b0a233ae"
    // 点点视频网关
    const val VIDEO_GATE_KEY = "23776862"
    const val VIDEO_GATE_SECRET = "b5ffc0cc02a74953ea9091338117feda"
    // 网页地址
    private const val BASE_URL = "https://service-student.cqebd.cn/"
    // 开始答题前查看页面，需试卷ID和任务ID
    const val URL_START_WORK: String = BASE_URL + "HomeWork/ExaminationPapers?id=%s&taskid=%s"
    // 答题结束查看页面，需试卷ID
    const val URL_END_WORK: String = BASE_URL + "HomeWork/CheckPaper?StudentQuestionsTasksId=%s"
    // 答题卡网页内容，
    const val URL_QUESTION: String = BASE_URL + "HomeWork/Question?id=%s&PapersID=%s&studentid=%s"
    // 小组长管理
    const val URL_LEADER: String = BASE_URL + "StudentGroup/task?GroupStudentId=%s"
    // 阅读消息，消息详情
    const val URL_MESSAGE: String = BASE_URL + "homework/msgdetails?id=%s"
    // 作业收藏页
    const val URL_COLLECT: String = BASE_URL + "studentCollect/StudentCollectList?studentid=%s"
    const val URL_COLLECT_ID: String =
        BASE_URL + "studentCollect/StudentCollectList?studentid=%s&SubjectTypeId=%d"
    // 错题每道题详情页
    const val URL_WRONG: String =
        BASE_URL + "HomeWork/ErrorQustionAnswer?QuestionID=%s&StudentQuestionsTasksId=%s"
    // 老师分享的试题详情页
    const val URL_SHARED: String = BASE_URL + "HomeWork/TaskShare?id=%d"
    // 学生反馈意见 回复
    const val URL_FEED_BACK: String = BASE_URL + "Help/Feedback?id=%s"
    // 隐私协议
    const val AGGREMENT: String = "http://app.zuoyouxue.com/privacy-policy.html"
    // 公告地址
    const val NOTICE: String = "http://ctsm.cqebd.cn/page/detail?id=%d"
    // 帮助
    const val HELP: String = "https://mp.weixin.qq.com/s/esrlSDLaI1J_aj-HzH7bZw"

}