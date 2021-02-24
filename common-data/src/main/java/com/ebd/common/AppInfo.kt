package com.ebd.common

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppInfo @Inject constructor(private val app: App) {

    private fun getPackageInfo(): PackageInfo? {
        return try {
            val pm = app.packageManager
            pm.getPackageInfo(app.packageName, PackageManager.GET_PERMISSIONS)
        } catch (e: Exception) {
            null
        }
    }

    fun versionName(): String {
        return getPackageInfo()?.versionName ?: "版本获取失败"
    }

    fun versionCode(): Long {
        return try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                (getPackageInfo()?.versionCode)?.toLong() ?: -1
            } else {
                getPackageInfo()?.longVersionCode ?: -1
            }
        } catch (e: Exception) {
            -1
        }
    }
}