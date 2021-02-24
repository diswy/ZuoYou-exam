package com.ebd.common.vo

import com.diswy.foundation.web.Resource
import com.ebd.common.web.ServerException

data class BaseResponse<out T>(
    val errorId: Int,
    val message: String,
    val isSuccess: Boolean,
    val data: T?
) {
    fun map(): Resource<T> {
        return if (this.isSuccess) {
            Resource.success(data)
        } else {
            Resource.error(ServerException(message))
        }
    }
}