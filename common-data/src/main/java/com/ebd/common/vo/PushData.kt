package com.ebd.common.vo

data class PushData(
    val total: Int,
    val pages: Int,// 总页码
    val index: Int,// 当前页码
    val dataList: List<PushMessage>
)

data class PushMessage(
    val id: Int,
    val createDatetime: String,
    val updateTime: String,
    val sysId: String,
    val articleTypeId: Int,
    val title: String,
    val content: String
)