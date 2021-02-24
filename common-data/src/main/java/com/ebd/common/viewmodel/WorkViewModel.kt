package com.ebd.common.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diswy.foundation.web.Resource
import com.ebd.common.config.EbdState
import com.ebd.common.repository.WorkRepository
import com.ebd.common.vo.Homework
import com.ebd.common.vo.MyWrong
import com.ebd.common.vo.SharedWork
import com.ebd.common.vo.WrongQuestion
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

class WorkViewModel @Inject constructor(
    private val workRepo: WorkRepository
) : ViewModel() {
    var status = EbdState.UNFINISHED
    var subjectId: Int? = null
    var gradeId: Int? = null

    val myHomework by lazy { MutableLiveData<Resource<List<Homework>>>() }
    val myWrong by lazy { MutableLiveData<Resource<List<MyWrong>>>() }
    val myWrongQuestion by lazy { MutableLiveData<Resource<WrongQuestion>>() }
    val sharedList by lazy { MutableLiveData<Resource<SharedWork>>() }

    private var jobHomework: Job? = null
    fun getHomeWork(page: Int) {
        jobHomework?.cancel()
        jobHomework = viewModelScope.launch {
            myHomework.value = Resource.loading()
            myHomework.value = workRepo.getHomeworkList(page, status, subjectId, gradeId)
        }
    }

    fun getWrongList(page: Int, subjectId: Int? = null) {
        viewModelScope.launch {
            myWrong.value = Resource.loading()
            myWrong.value = workRepo.getWrongList(page, subjectId)
        }
    }

    fun getWrongQuestion(id: Int) {
        viewModelScope.launch {
            myWrongQuestion.value = Resource.loading()
            myWrongQuestion.value = workRepo.getWrongQuestion(id)
        }
    }

    fun getTeacherShared(page: Int) {
        viewModelScope.launch {
            sharedList.value = Resource.loading()
            sharedList.value = workRepo.getTeacherShared(page, subjectId)
        }
    }

}