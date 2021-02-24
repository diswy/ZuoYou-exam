package com.ebd.common.vo

data class Video(
    val EndDateTime: String,
    val GradeId: Int,
    val GradeName: String,
    val Id: Int,
    val IsFeedback: Boolean,
    val IsStudentCollect: Boolean,
    val Name: String,
    val PeriodCount: Int,
    val RegisterNumber: Int,
    val SchoolId: Int,
    val SchoolName: String,
    val SchoolTermTypeId: Int,
    val SchoolTermTypeName: String,
    val Snapshoot: String,
    val StartDate: String,
    val Status: Int,
    val SubjectTypeId: Int,
    val SubjectTypeName: String,
    val TeacherId: Int,
    val TeacherName: String,
    val TeacherPhoto: String,
    val TeachingMaterialTypeName: String,
    val Type: Int
)