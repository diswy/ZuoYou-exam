package com.ebd.common.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diswy.foundation.tools.UpdateHelper
import com.diswy.foundation.web.Resource
import com.ebd.common.AppInfo
import com.ebd.common.repository.CommonRepository
import com.ebd.common.vo.*
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

class CommonViewModel @Inject constructor(
    private val repo: CommonRepository,
    private val appInfo: AppInfo,
    private val updateHelper: UpdateHelper
) : ViewModel() {

    val ncUrl by lazy { MutableLiveData<Resource<String>>() }
    val setting by lazy { MutableLiveData<Resource<String>>() }
    val user by lazy { MutableLiveData<Resource<User>>() }
    val findUser by lazy { MutableLiveData<Resource<FindUser>>() }
    val phoneCode by lazy { MutableLiveData<Resource<BaseResponse<Unit>>>() }
    val newPwd by lazy { MutableLiveData<Resource<BaseResponse<Unit>>>() }
    val honorList by lazy { MutableLiveData<Resource<List<Honor>>>() }
    val myHonor by lazy { MutableLiveData<Resource<List<HonorMe>>>() }
    val message by lazy { MutableLiveData<Resource<Message>>() }
    val feedBack by lazy { MutableLiveData<Resource<BaseResponse<Unit>>>() }
    val avatar by lazy { MutableLiveData<Resource<String>>() }
    val bindPhone by lazy { MutableLiveData<Resource<BaseResponse<Unit>>>() }

    fun getAppInfo() = appInfo

    fun login(account: String, password: String) {
        viewModelScope.launch {
            user.value = Resource.loading()
            user.value = repo.login(account, password)
        }
    }

    fun getMsgList(page: Int) {
        viewModelScope.launch {
            message.value = Resource.loading()
            message.value = repo.getMsgList(page)
        }
    }

    fun readMessage(type: Int, id: String) {
        viewModelScope.launch {
            repo.readMessage(type, id)
        }
    }

    fun getHonor() {
        viewModelScope.launch {
            honorList.value = Resource.loading()
            honorList.value = repo.getHonor()
        }
    }

    fun getMyHonor() {
        viewModelScope.launch {
            myHonor.value = Resource.loading()
            myHonor.value = repo.getMyHonor()
        }
    }

    fun findUser(idCard: String) {
        viewModelScope.launch {
            findUser.value = Resource.loading()
            findUser.value = repo.findUser(idCard)
        }
    }

    fun getPhoneCode(loginName: String, type: Int) {
        viewModelScope.launch {
            phoneCode.value = Resource.loading()
            phoneCode.value = repo.getPhoneCode(loginName, type)
        }
    }

    fun bindPhone(status: Int, phone: String, code: String, password: String) {
        viewModelScope.launch {
            bindPhone.value = Resource.loading()
            bindPhone.value = repo.bindPhone(status, code, phone, password)
        }
    }

    fun updatePwd(user: String, pwd: String, code: String) {
        viewModelScope.launch {
            newPwd.value = Resource.loading()
            newPwd.value = repo.updatePwd(user, pwd, code)
        }
    }

    fun modifyPwd(old: String, new: String) {
        viewModelScope.launch {
            newPwd.value = Resource.loading()
            newPwd.value = repo.modifyPwd(old, new)
        }
    }

    fun feedBack(content: String, classify: String) {
        viewModelScope.launch {
            feedBack.value = Resource.loading()
            feedBack.value = repo.feedBack(content, classify)
        }
    }

    fun updateAvatar(context: Context, imageFile: File) {
        viewModelScope.launch {
            avatar.value = Resource.loading()
            avatar.value = repo.updateAvatar(context, imageFile)
        }
    }

    fun getConfig() {
        viewModelScope.launch {
            setting.value = Resource.loading()
            setting.value = repo.getSettings("ddk-zyx-phone")
        }
    }

    fun update(ctx: Context, url: String, title: String, description: String) {
        updateHelper.download(ctx, url, title, description)
    }

    fun getHeartQuestion(){
        viewModelScope.launch {
            ncUrl.value = Resource.loading()
            ncUrl.value = repo.getSettings("DYBCWJ")
        }
    }
}