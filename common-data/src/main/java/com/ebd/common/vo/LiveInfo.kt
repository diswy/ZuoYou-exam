package com.ebd.common.vo

data class LiveInfo(
    var Id: Int,
    var PeriodId: Int,
    var CreateDateTime: String,
    var ChannelID: String,
    var ChannelName: String,
    var ChannelPushUrl: String,
    var ChannelPullUrls: String,
    var ChatRoomId: String,
    var ChatRoomName: String
)