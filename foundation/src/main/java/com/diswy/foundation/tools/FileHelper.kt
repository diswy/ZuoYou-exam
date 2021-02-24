package com.diswy.foundation.tools

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.text.TextUtils
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import java.util.*

object FileHelper {
    val parent =
        Environment.getExternalStorageDirectory().absolutePath.plus("/CloudClass/document/")

    fun getDocParent(): File {
        return File(parent)
    }

    /**
     * 检测父文件夹，没有就创建
     */
    fun makeParentFile() {
        val parent = File(parent)
        if (!parent.exists()) {
            parent.mkdirs()
        }
    }

    fun isFileExists(fileName: String): Boolean {
        return File(parent + fileName).exists()
    }

    fun getFileByName(fileName: String): File {
        return File(parent + fileName)
    }


    /**
     * 调用系统应用打开图片
     *
     * @param context context
     * @param file    file
     */
    fun openFile(context: Context, file: File) {
        val intent = Intent()
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        //设置intent的Action属性
        intent.action = Intent.ACTION_VIEW
        // 支持Android7.0，Android 7.0以后，用了Content Uri 替换了原本的File Uri
        val uri: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val appId = context.applicationInfo.packageName
            FileProvider.getUriForFile(context, "${appId}.provider", file)
        } else {
            Uri.fromFile(file)
        }
        //获取文件file的MIME类型
        val type = getMIMEType(file)
        //设置intent的data和Type属性。
        intent.setDataAndType(uri, type)
        //跳转
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "找不到打开此文件的应用！", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 根据文件后缀回去MIME类型
     *
     * @param file file
     * @return string
     */
    private fun getMIMEType(file: File): String {
        var type = "*/*"
        val fName = file.name
        //获取后缀名前的分隔符"."在fName中的位置。
        val dotIndex = fName.lastIndexOf(".")
        if (dotIndex < 0) {
            return type
        }
        /* 获取文件的后缀名*/
        val end = fName.substring(dotIndex).toLowerCase(Locale.CHINA)
        if (TextUtils.isEmpty(end)) {
            return type
        }
        //在MIME和文件类型的匹配表中找到对应的MIME类型。
        for (strings in MIME_MapTable) {
            if (end == strings[0]) {
                type = strings[1]
                break
            }
        }
        return type
    }

    private val MIME_MapTable =
        arrayOf(
            arrayOf(".3gp", "video/3gpp"),
            arrayOf(".apk", "application/vnd.android.package-archive"),
            arrayOf(".asf", "video/x-ms-asf"),
            arrayOf(".avi", "video/x-msvideo"),
            arrayOf(".bin", "application/octet-stream"),
            arrayOf(".bmp", "image/bmp"),
            arrayOf(".c", "text/plain"),
            arrayOf(".class", "application/octet-stream"),
            arrayOf(".conf", "text/plain"),
            arrayOf(".cpp", "text/plain"),
            arrayOf(".doc", "application/msword"),
            arrayOf(
                ".docx",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            ),
            arrayOf(".xls", "application/vnd.ms-excel"),
            arrayOf(
                ".xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            ),
            arrayOf(".exe", "application/octet-stream"),
            arrayOf(".gif", "image/gif"),
            arrayOf(".gtar", "application/x-gtar"),
            arrayOf(".gz", "application/x-gzip"),
            arrayOf(".h", "text/plain"),
            arrayOf(".htm", "text/html"),
            arrayOf(".html", "text/html"),
            arrayOf(".jar", "application/java-archive"),
            arrayOf(".java", "text/plain"),
            arrayOf(".jpeg", "image/jpeg"),
            arrayOf(".jpg", "image/jpeg"),
            arrayOf(".js", "application/x-javascript"),
            arrayOf(".log", "text/plain"),
            arrayOf(".m3u", "audio/x-mpegurl"),
            arrayOf(".m4a", "audio/mp4a-latm"),
            arrayOf(".m4b", "audio/mp4a-latm"),
            arrayOf(".m4p", "audio/mp4a-latm"),
            arrayOf(".m4u", "video/vnd.mpegurl"),
            arrayOf(".m4v", "video/x-m4v"),
            arrayOf(".mov", "video/quicktime"),
            arrayOf(".mp2", "audio/x-mpeg"),
            arrayOf(".mp3", "audio/x-mpeg"),
            arrayOf(".mp4", "video/mp4"),
            arrayOf(".mpc", "application/vnd.mpohun.certificate"),
            arrayOf(".mpe", "video/mpeg"),
            arrayOf(".mpeg", "video/mpeg"),
            arrayOf(".mpg", "video/mpeg"),
            arrayOf(".mpg4", "video/mp4"),
            arrayOf(".mpga", "audio/mpeg"),
            arrayOf(".msg", "application/vnd.ms-outlook"),
            arrayOf(".ogg", "audio/ogg"),
            arrayOf(".pdf", "application/pdf"),
            arrayOf(".png", "image/png"),
            arrayOf(".pps", "application/vnd.ms-powerpoint"),
            arrayOf(".ppt", "application/vnd.ms-powerpoint"),
            arrayOf(
                ".pptx",
                "application/vnd.openxmlformats-officedocument.presentationml.presentation"
            ),
            arrayOf(".prop", "text/plain"),
            arrayOf(".rc", "text/plain"),
            arrayOf(".rmvb", "audio/x-pn-realaudio"),
            arrayOf(".rtf", "application/rtf"),
            arrayOf(".sh", "text/plain"),
            arrayOf(".tar", "application/x-tar"),
            arrayOf(".tgz", "application/x-compressed"),
            arrayOf(".txt", "text/plain"),
            arrayOf(".wav", "audio/x-wav"),
            arrayOf(".wma", "audio/x-ms-wma"),
            arrayOf(".wmv", "audio/x-ms-wmv"),
            arrayOf(".wps", "application/vnd.ms-works"),
            arrayOf(".xml", "text/plain"),
            arrayOf(".z", "application/x-compress"),
            arrayOf(".zip", "application/x-zip-compressed"),
            arrayOf("", "*/*")
        )
}