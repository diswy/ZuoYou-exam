package com.ebd.common.repository

import com.diswy.foundation.web.Resource
import com.ebd.common.App
import com.ebd.common.config.EbdState
import com.ebd.common.vo.DocData
import com.ebd.common.vo.LibPermission
import com.ebd.common.vo.PushData
import com.ebd.common.web.WorkService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ToolRepository @Inject constructor(
    private val workService: WorkService
) {
    /**
     * 记录日志
     * @return 是否成功
     */
    suspend fun userLog(params: HashMap<String, String>): Boolean {
        return try {
            workService.userLog(params).isSuccess
        } catch (e: Exception) {
            App.instance.handleExceptions(e)
            false
        }
    }

    /**
     * 验证是否有赶考网权限
     */
    suspend fun getGanPer(): Resource<LibPermission> {
        return try {
            workService.getGanPer().map()
        } catch (e: Exception) {
            Resource.error(e)
        }
    }

    /**
     * 获取推送消息
     */
    suspend fun getPushList(page: Int): Resource<PushData> {
        return try {
            workService.getPushList(page).map()
        } catch (e: Exception) {
            Resource.error(e)
        }
    }

    /**
     * 获取推送资料，类型不限，可自由下载
     */
    suspend fun getDoc(page: Int): Resource<DocData> {
        return try {
            workService.getDoc(page).map()
        } catch (e: Exception) {
            Resource.error(e)
        }
    }

    /**
     * @return true 有作业 false 没有作业
     */
    suspend fun getHomeworkList(): Resource<Boolean> {
        return try {
            val list = workService.getWorkList(1, EbdState.NEW_WORK)
            Resource.success(!list.data.isNullOrEmpty())
        } catch (e: Exception) {
            Resource.error(e)
        }

    }
}