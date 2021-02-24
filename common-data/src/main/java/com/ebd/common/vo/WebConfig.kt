package com.ebd.common.vo

/**
 * 远端配置
 */
data class WebConfig(
    val soeCoeff: String,// 口语评测苛刻指数
    val launchUrl: String,// 启动页图片
    val launchPad: String,// 启动页图片-平板
    val update: Update// 更新
)

data class Update(
    val versionCode: Int,
    val versionName: String,
    val description: String,
    val forceUpdate: Boolean,
    val downloadUrl: String
)