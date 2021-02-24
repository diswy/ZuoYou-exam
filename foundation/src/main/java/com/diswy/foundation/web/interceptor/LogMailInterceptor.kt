package com.diswy.foundation.web.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber

class LogMailInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
//        Timber.d(request.url().toString())
//        Timber.d(request.toString())
        val response: Response
        try {
            response = chain.proceed(request)
        } catch (e: Exception) {
            Timber.e(e.toString())
            throw e
        }
        return response
    }
}