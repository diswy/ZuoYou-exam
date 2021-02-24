package com.ebd.common.vo

/**
 * 试题信息
 */
data class QuestionPaper(
    val Id: Int,
    val Name: String,
    val SubjectTypeName: String,
    val SubjectTypeId: Int,
    val Count: Int,
    val QuestionGruop: List<QuestionGroup> = ArrayList()
)

data class QuestionGroup(
    val Id: Int,
    val Name: String,
    val Question: List<QuestionInfo> = ArrayList()
)

data class QuestionInfo(
    val Id: Int,
    val QuestionTypeId: Int,
    val AlternativeContent: String,
    val AnswerType: String,
    val Fraction: Float,
    val Sort: Int,
    val StudentsAnswer: String?,// 学生未作答时，后台返回的答案为空
    val WriteType: Int?,
    val RespondTime: Long,
    val Subject: String = "",
    val QuestionSubjectAttachment: List<Attachment> = ArrayList()
)