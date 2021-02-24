package com.ebd.common.vo.local

/**
 * 单选题，后台给出的选项
 * @property Id A、B、C、D
 * @property content 选项具体内容，目前不使用
 */
data class SingleOptions(val Id: String, val content: String)

/**
 * 用户作答的答案
 * @property Answer 答案文本
 * @property Id 答案序号
 * @property TypeId 题目类型
 */
data class SAnswer(val Id: Int, val TypeId: Int, var Answer: String = "")

/**
 * 答题卡格式
 * @property Id 答案序号，每张答题卡可构建多列，通常为1
 * @property TypeId 题目类型
 */
data class AnswerType(val Id: String, val TypeId: Int)


