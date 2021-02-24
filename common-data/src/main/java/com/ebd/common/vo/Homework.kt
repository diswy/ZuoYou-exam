package com.ebd.common.vo

import android.os.Parcelable
import com.ebd.common.config.EbdState
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * 作业列表信息
 * Created by @author xiaofu on 2019/2/21.
 */
@Parcelize
data class Homework(
    @SerializedName("StudentQuestionsTasksID")
    val TaskId: Long,
    @SerializedName("ExaminationPapersId")
    val PapersId: Long,
    @SerializedName("ExaminationPapersPushId")
    val PushId: Long,
    @SerializedName("ExaminationPapersTypeId")
    val TypeId: Int,
    @SerializedName("PapersTypeName")
    val TypeName: String,
    @SerializedName("PuchDateTime")
    var publishTime: String,
    @SerializedName("ExaminationPapersAttachment")
    val attachments: List<Attachment>?,
    @SerializedName("SubjectTypeId")
    val SubjectId: Int,
    @SerializedName("SubjectTypeName")
    val SubjectName: String,
    var Status: Int,
    val IsTasks: Boolean,
    val Name: String,
    val CanStartDateTime: String,
    var StartTime: String?,
    val CanEndDateTime: String,
    val EndTime: String?,
    val Count: Int,
    var Duration: Int,
    val Fraction: Double?,
    val IsMedal: Boolean
) : Parcelable {
    fun getWorkStatus(): String {
        return when (Status) {
            EbdState.NEW_WORK -> "新作业"
            EbdState.WORKING -> "答题中"
            EbdState.WORK_DOWN -> "已完成"
            EbdState.WORK_READ -> "已批阅"
            else -> "未知"
        }
    }
}

@Parcelize
data class Attachment(
    val Id: Int,
    val ExaminationPapersId: Int,
    val Status: Int,
    val Type: Int,
    val QuestionId: Int,
    val AnswerType: Int,// 1播放中答、2是必须播放完后可答
    val CanWatchTimes: Int,// 最多观看次数，0无限制
    val Name: String,
    val MediaTypeName: String,
    val CreateDateTime: String,
    val Remarks: String?,
    val Url: String,
    val VideoId: String?
) : Parcelable