package com.ebd.common.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diswy.foundation.web.Resource
import com.ebd.common.cache.CacheTool
import com.ebd.common.cache.loginId
import com.ebd.common.repository.VideoRepository
import com.ebd.common.vo.LiveInfo
import com.ebd.common.vo.Period
import com.ebd.common.vo.PeriodInfo
import com.ebd.common.vo.Video
import kotlinx.coroutines.launch
import javax.inject.Inject

class VideoViewModel @Inject constructor(
    private val videoRepo: VideoRepository
) : ViewModel() {

    val video by lazy { MutableLiveData<Resource<List<Video>>>() }
    val period by lazy { MutableLiveData<Resource<List<Period>>>() }
    val schedule by lazy { MutableLiveData<Resource<List<Period>>>() }
    val periodInfo by lazy { MutableLiveData<Resource<PeriodInfo>>() }
    val liveInfo by lazy { MutableLiveData<Resource<LiveInfo>>() }
    val vodRecord by lazy { MutableLiveData<Resource<Boolean>>() }
    var subjectId: Int? = null
    var gradeId: Int? = null

    fun getVideo() {
        viewModelScope.launch {
            video.value = Resource.loading()
            video.value = videoRepo.getVideoList(subjectId, gradeId)
        }
    }

    fun getPeriod(videoId: Int) {
        viewModelScope.launch {
            period.value = Resource.loading()
            period.value = videoRepo.getVideoPeriod(videoId)
        }
    }

    fun getSchedulePeriod(year: Int, month: Int) {
        viewModelScope.launch {
            schedule.value = Resource.loading()
            schedule.value = videoRepo.getVideoPeriodByMonth("%d-%d".format(year, month))
        }
    }

    fun getPeriodInfo(periodId: Int) {
        viewModelScope.launch {
            periodInfo.value = Resource.loading()
            periodInfo.value = videoRepo.getPeriodInfo(periodId)
        }
    }

    fun getLiveInfo(periodId: Int) {
        viewModelScope.launch {
            liveInfo.value = Resource.loading()
            liveInfo.value = videoRepo.getLiveInfo(periodId)
        }
    }

    /**
     * 观看记录
     * @param periodId 课时ID
     * @param duration 累计观看时长
     * @param silent 静默记录
     */
    fun playRecord(periodId: Int, duration: Int, silent: Boolean = true) {
        viewModelScope.launch {
            CacheTool.setValue("vod_${loginId}_${periodId}" to duration)
            if (!silent) {
                vodRecord.value = Resource.loading()
                vodRecord.value = videoRepo.playRecord(periodId, duration)
            } else {
                videoRepo.playRecord(periodId, duration)
            }
        }
    }
}