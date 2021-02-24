package com.ebd.common.vo

/**
 * 开始做作业返回的信息
 */
data class StartWorkInfo(
    val startDate: String,// 开始答题时间
    val submitMode: Int,// 提交模式
    val nowDate: Long// 服务器时间
)