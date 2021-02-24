package com.ebd.common.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diswy.foundation.web.Resource
import com.ebd.common.repository.CoreRepository
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

class ImgViewModel @Inject constructor(
    private val coreRepo: CoreRepository
) : ViewModel() {
    // 上传图片后的地址
    private val mImgUrl = MutableLiveData<Resource<String>>()

    fun getImg() = mImgUrl

    /**
     * 图片答案上传
     */
    fun uploadImg(context: Context, imageFile: File) {
        viewModelScope.launch {
            mImgUrl.value = Resource.loading()
            mImgUrl.value = coreRepo.uploadImg(context, imageFile)
        }
    }
}