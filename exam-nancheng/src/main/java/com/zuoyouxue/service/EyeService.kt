package com.zuoyouxue.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import timber.log.Timber

/**
 * 健康用眼。定时弹出提示
 */
class EyeService : Service() {

//    if (!Settings.canDrawOverlays(this)) {
//        Timber.d("弹窗：没有权限")
//        startActivity(
//            Intent(
//            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
//            "package:${packageName}".toUri()
//        )
//        )
//    } else {
//        Timber.d("弹窗:拥有弹窗权限")
//        Intent(this, EyeService::class.java).also { intent ->
//            startService(intent)
//        }
//    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.d("服务：启动")
        return START_NOT_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        stopSelf()
        super.onTaskRemoved(rootIntent)
    }

}
