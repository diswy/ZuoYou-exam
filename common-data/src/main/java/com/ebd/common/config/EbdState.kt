package com.ebd.common.config

object EbdState {
    const val RELOAD = 233      // 重新登录

    const val FIND_PWD = 0      // 找回密码
    const val BIND_USER = 1     // 绑定账号
    /**
     * 作业状态
     */
    const val NEW_WORK = -1     // 新作业
    const val WORKING = 0       // 答题中
    const val WORK_DOWN = 1     // 已完成
    const val WORK_READ = 2     // 已批阅

    const val UNFINISHED = 10   // 包含未完成和作答中
    const val FINISHED = 12     // 包含已完成和已批阅

    /**
     * 视频状态
     */
    const val VIDEO_NOT_START = 0   // 未播
    const val VIDEO_LIVE = 1        // 直播中
    const val VIDEO_LIVE_END = 2    // 直播结束
    const val VIDEO_VOD = 3         // 转码结束可点播

    /**
     * 学科
     */
    const val Chinese = 13
    const val Math = 7
    const val English = 12
    const val Physics = 9
    const val Chemistry = 2
    const val Music = 11
    const val History = 3
    const val Geography = 1
    const val Biology = 5
    const val Politics = 14
    const val Science = 16
    const val all = 17
    const val Composite = 22
    const val Internet = 10
}