package com.ebd.common.vo

data class SharedWork(
    val DataCount: Int,
    val PageIndex: Int,
    val PageSieze: Int,
    val DataList: List<SharedWorkItem>
) {
    data class SharedWorkItem(
        val Id: Int,
        val StudentId: Int,
        val PapersId: Int,
        val SubjectTypeId: Int,
        val StudentName: String,
        val PapersName: String,
        val AnswerId: String,
        val SubjectImage: String,
        val QuestionId: Int,
        val Subject: String,
        val AnswerHtml: String?,
        val QuestionTypeId: Int,
        val QuestionTypeName: String,
        val CreateDateTime: String,
        val StudentQuestionsTasksId: Int,
        val Photo: String,
        val TeacherName: String,
        val PapersQuestion: String
    )
}