package com.diswy.foundation.throwable

/**
 * 答案丢失
 */
class LostAnswerException(msg: String = "答案提交失败，请检查网络后重试") : Throwable(msg)