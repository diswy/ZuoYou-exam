package com.ebd.common.vo

data class WrongQuestion(
    val StudentQuestionsTasksID: Int,
    val ExaminationPapersID: Int,
    val Count: Int,
    val ExaminationPapersName: String,
    val ErrorList: List<WrongQuestionItem>
) {
    data class WrongQuestionItem(
        val querstionId: Int,
        val sortId: Int
    )
}