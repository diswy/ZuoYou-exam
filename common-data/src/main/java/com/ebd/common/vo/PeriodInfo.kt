package com.ebd.common.vo

data class PeriodInfo(
    val CanPlayDateTime: String,
    val CourseId: Int,
    val Day: String,
    val Durartion: Int,
    val GradeId: Int,
    val HasChannel: Boolean,
    val HasChat: Boolean,
    val HasIWB: Boolean,
    val HasVchat: Boolean,
    val Id: Int,
    val IsFeedback: Boolean,
    val LiveProvider: String,
    val Name: String,
    val PlanStartDate: String,
    val SchoolId: Int,
    val Status: Int,
    val SubjectTypeId: Int,
    val TeacherId: Int,
    val TeachingMaterialSectionId: Int,
    val TeachingMaterialTypeId: Int,
    val Type: Int,
    val ViewType: Int,
    val VodPlayList: List<VodPlay>
)

data class VodPlay(
    val Definition: Int,
    val Url: String,
    val VBitrate: Int,
    val VHeight: Int,
    val VWidth: Int
)