package com.ebd.common.vo

data class Message(
    val total: Int,
    val pages: Int,
    val index: Int,
    val dataList: List<MsgData>
)

data class MsgData(
    val Id: String,
    val Title: String,
    val Content: String,
    val CreateDateTime: String,
    var Status: Int,
    val Type: Int,
    val Images: String,
    val StudentId: Int
)