package com.ebd.common.vo

data class PackAnswerCommon(
    val data: PackAnswer,
    val name: String = "answer",
    val version: Double = 1.1
)

data class PackAnswer(
    val Stu_Id: Int,
    val TaskId: Long,
    val Status: Int,//-1 默认,0做题中,1交卷
    val ExaminationPapersPushId: Long,
    val ExaminationPapersId: Long,
    val Version: String,
    val Common: String,
    val Source: String,
    val AnswerList: List<PAnswer>
)

data class PAnswer(
    val QuestionId: Int,
    val QuestionTypeId: Int,
    val Answer: String
)