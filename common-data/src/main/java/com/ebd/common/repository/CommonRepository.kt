package com.ebd.common.repository

import android.content.Context
import android.os.Build
import com.diswy.foundation.throwable.UploadException
import com.diswy.foundation.web.Resource
import com.ebd.common.AppInfo
import com.ebd.common.cache.updateUserAvatar
import com.ebd.common.vo.*
import com.ebd.common.web.VideoService
import com.ebd.common.web.WorkService
import okhttp3.MediaType
import okhttp3.RequestBody
import top.zibin.luban.Luban
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommonRepository @Inject constructor(
    private val workService: WorkService,
    private val videoService: VideoService,
    private val info: AppInfo
) {

    suspend fun getSettings(key: String): Resource<String> {
        return try {
            workService.getSetting(key).map()
        } catch (e: Exception) {
            Resource.error(e)
        }
    }

    suspend fun login(account: String, password: String): Resource<User> {
        return try {
            workService.userLogin(account, password).map()
        } catch (e: Exception) {
            Resource.error(e)
        }
    }

    suspend fun getMsgList(page: Int): Resource<Message> {
        return try {
            workService.getMsgList(page).map()
        } catch (e: Exception) {
            Resource.error(e)
        }
    }

    suspend fun readMessage(type: Int, id: String) {
        try {
            workService.readMessage(type, id)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun getHonor(): Resource<List<Honor>> {
        return try {
            workService.getHonor().map()
        } catch (e: Exception) {
            Resource.error(e)
        }
    }

    suspend fun getMyHonor(): Resource<List<HonorMe>> {
        return try {
            workService.getMyHonor().map()
        } catch (e: Exception) {
            Resource.error(e)
        }
    }

    suspend fun findUser(idCard: String): Resource<FindUser> {
        return try {
            workService.findUser(idCard).map()
        } catch (e: Exception) {
            Resource.error(e)
        }
    }

    suspend fun getPhoneCode(loginName: String, type: Int): Resource<BaseResponse<Unit>> {
        return try {
            Resource.success(workService.getPhoneCode(loginName, type))
        } catch (e: Exception) {
            Resource.error(e)
        }
    }

    suspend fun bindPhone(status: Int, code: String, tel: String, pwd: String)
            : Resource<BaseResponse<Unit>> {
        return try {
            Resource.success(workService.bindPhone(status, code, tel, pwd))
        } catch (e: Exception) {
            Resource.error(e)
        }
    }

    suspend fun updatePwd(user: String, pwd: String, code: String): Resource<BaseResponse<Unit>> {
        return try {
            Resource.success(workService.updatePwd(user, pwd, code))
        } catch (e: Exception) {
            Resource.error(e)
        }
    }

    suspend fun modifyPwd(old: String, new: String): Resource<BaseResponse<Unit>> {
        return try {
            Resource.success(workService.modifyPwd(old, new))
        } catch (e: Exception) {
            Resource.error(e)
        }
    }

    suspend fun feedBack(content: String, classify: String): Resource<BaseResponse<Unit>> {
        return try {
            val source = info.versionName() + "|" + Build.MODEL
            Resource.success(workService.submitFeedBk(content, classify, source))
        } catch (e: Exception) {
            Resource.error(e)
        }
    }

    suspend fun updateAvatar(context: Context, imageFile: File): Resource<String> {
        return try {
            // 压缩图片
            val zipImage = Luban.with(context).load(imageFile).get()[0]
            val requestBody = RequestBody.create(MediaType.parse("image/jpeg"), zipImage)
            // 上传图片
            val result = workService.uploadFile(requestBody)
            if (result.isSuccess) {
                // 更新头像地址并写入本地缓存
                videoService.updateAvatar(result.data!!)
                updateUserAvatar(result.data)
                Resource.success(result.data)
            } else {
                Resource.error(UploadException(result.message))
            }
        } catch (e: Exception) {
            Resource.error(e)
        }
    }

}