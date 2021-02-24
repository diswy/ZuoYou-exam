package com.diswy.foundation.tools

import android.app.DownloadManager
import android.app.DownloadManager.Request
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.diswy.foundation.quick.toast
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class UpdateHelper @Inject constructor() {

    private val mDownloadReceiver = DownloadReceiver()

    private lateinit var mDownloadManager: DownloadManager

    private var mDownloadTaskId: Long = 0L

    private inner class DownloadReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            context ?: return
            intent ?: return

            when (intent.action) {
                DownloadManager.ACTION_DOWNLOAD_COMPLETE -> {
                    install(context)
                }
                DownloadManager.ACTION_NOTIFICATION_CLICKED -> {
                }
            }

        }
    }

    fun download(ctx: Context, url: String, title: String, description: String) {
        ctx.toast("开始下载")
        val request = Request(url.toUri())
        request.setNotificationVisibility(Request.VISIBILITY_VISIBLE)
        request.setTitle(title)
        request.setDescription(description)
        request.setVisibleInDownloadsUi(true)
        request.setMimeType("application/vnd.android.package-archive")
        val file = File(ctx.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "latest.apk")
        if (file.exists()) {
            file.delete()
        }
        request.setDestinationUri(file.toUri())
        mDownloadManager = ctx.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        mDownloadTaskId = mDownloadManager.enqueue(request)

        println("-----------!@#->UpdateHelper:context=$ctx")
        println("-----------!@#->UpdateHelper:mDownloadTaskId=$mDownloadTaskId")
        println("-----------!@#->UpdateHelper:file=${file.absolutePath}")
        val intentFilter = IntentFilter()
        intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        intentFilter.addAction(DownloadManager.ACTION_NOTIFICATION_CLICKED)
        ctx.registerReceiver(mDownloadReceiver, intentFilter)
    }

    private fun install(ctx: Context) {
        val file = File(ctx.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "latest.apk")

        val intent = Intent("android.intent.action.VIEW")
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val apkUri = FileProvider.getUriForFile(
                ctx, ctx.applicationContext.packageName + ".provider", file
            )
            intent.addCategory("android.intent.category.DEFAULT")
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive")
        } else {
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive")
        }
        ctx.startActivity(intent)
    }
}