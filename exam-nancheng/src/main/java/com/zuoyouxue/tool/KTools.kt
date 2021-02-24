package com.zuoyouxue.tool

import com.ebd.common.vo.VodPlay
import kit.diswy.vo.VodDefinition

/**
 * @return 处理后的视频
 */
fun filterVodUrl(vodPlay: List<VodPlay>): ArrayList<VodDefinition> {
    val mp4List = vodPlay.filter { it.Url.endsWith(".mp4") }// 取出mp4文件
        .sortedByDescending { it.VBitrate }// 码率由高->低排序

    val vodList = ArrayList<VodDefinition>()

    mp4List.forEachIndexed { index, item ->
        when (index) {
            0 -> vodList.add(VodDefinition(item.VBitrate, "1080P", item.Url))
            1 -> vodList.add(VodDefinition(item.VBitrate, "720P", item.Url))
            2 -> vodList.add(VodDefinition(item.VBitrate, "480P", item.Url))
            3 -> vodList.add(VodDefinition(item.VBitrate, "360P", item.Url))
        }
    }
//    mp4List.forEach {
//        when (it.Definition) {
//            0 -> vodList.add(VodDefinition(it.VBitrate, "720P", it.Url))
//            10 -> vodList.add(VodDefinition(it.VBitrate, "360P", it.Url))
//            20 -> vodList.add(VodDefinition(it.VBitrate, "480P", it.Url))
//            30 -> vodList.add(VodDefinition(it.VBitrate, "1080P", it.Url))
//        }
//    }
    return vodList
}