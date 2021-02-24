package com.diswy.foundation.throwable

/**
 * 图片上传错误
 */
class UploadException(msg: String = "图片上传失败") : Throwable(msg)