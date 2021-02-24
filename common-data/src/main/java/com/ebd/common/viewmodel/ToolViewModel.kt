package com.ebd.common.viewmodel

import android.content.Context
import android.net.wifi.WifiManager
import android.os.Build
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diswy.foundation.tools.AppExecutors
import com.diswy.foundation.web.Resource
import com.ebd.common.App
import com.ebd.common.AppInfo
import com.ebd.common.cache.getUser
import com.ebd.common.repository.ToolRepository
import com.ebd.common.vo.DocData
import com.ebd.common.vo.LibPermission
import com.ebd.common.vo.PushData
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.*
import javax.inject.Inject

class ToolViewModel @Inject constructor(
    private val toolRepo: ToolRepository,
    private val app: App,
    private val appInfo: AppInfo,
    private val appExecutors: AppExecutors
) : ViewModel() {

    val ganPer by lazy { MutableLiveData<Resource<LibPermission>>() }
    val pushData by lazy { MutableLiveData<Resource<PushData>>() }
    val newWork by lazy { MutableLiveData<Resource<Boolean>>() }
    val docData by lazy { MutableLiveData<Resource<DocData>>() }
    val switchWorkTab by lazy { MutableLiveData<Boolean>() }
    val switchVideoTab by lazy { MutableLiveData<Boolean>() }

    fun switchWork() {
        switchWorkTab.value = true
        switchWorkTab.value = false
    }

    fun switchVideo() {
        switchVideoTab.value = true
        switchVideoTab.value = false
    }

    fun getAppInfo(): AppInfo {
        return appInfo
    }

    fun getGanPer() {
        viewModelScope.launch {
            ganPer.value = Resource.loading()
            ganPer.value = toolRepo.getGanPer()
        }
    }

    fun getHomeData() {
        viewModelScope.launch {
            pushData.value = Resource.loading()
            newWork.value = Resource.loading()
            newWork.value = toolRepo.getHomeworkList()
            pushData.value = toolRepo.getPushList(1)
        }
    }

    fun getDoc(page: Int) {
        viewModelScope.launch {
            docData.value = Resource.loading()
            docData.value = toolRepo.getDoc(page)
        }
    }

    /**
     * 用户记录，获取IP
     */
    fun userLog() {
        // 获取WiFi服务
        val wifiManager = app.getSystemService(Context.WIFI_SERVICE) as WifiManager
        // 判断WiFi是否开启
        if (wifiManager.isWifiEnabled) {// 使用wifi，也可能没有网
            appExecutors.networkIO().execute {
                val line: String
                val infoUrl: URL
                val inStream: InputStream
                try {
                    infoUrl = URL("http://pv.sohu.com/cityjson?ie=utf-8")
                    val connection = infoUrl.openConnection()
                    val httpConnection = connection as HttpURLConnection
                    val responseCode = httpConnection.responseCode
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        inStream = httpConnection.inputStream
                        val reader = BufferedReader(InputStreamReader(inStream, "utf-8"))
                        val strber = StringBuilder()
                        reader.lineSequence().forEach {
                            strber.append(it + "\n")
                        }
                        inStream.close()
                        // 从反馈的结果中提取出IP地址
                        val start = strber.indexOf("{")
                        val end = strber.indexOf("}")
                        val json = strber.substring(start, end + 1)
                        if (json != null) {
                            try {
                                val jsonObject = JSONObject(json)
                                line = jsonObject.optString("cip")
                                log(line)
                            } catch (e: JSONException) {
                                log("未知IP")
                            }
                        }
                    }
                } catch (e: MalformedURLException) {
                    log("未知IP")
                } catch (e: IOException) {
                    log("未知IP")
                }
            }

        } else {// 使用流量
            appExecutors.networkIO().execute {
                try {
                    var networkInterface: NetworkInterface
                    var iNetAddress: InetAddress
                    val en = NetworkInterface.getNetworkInterfaces()
                    while (en.hasMoreElements()) {
                        networkInterface = en.nextElement()
                        val enumIpAdd = networkInterface.inetAddresses
                        while (enumIpAdd.hasMoreElements()) {
                            iNetAddress = enumIpAdd.nextElement()
                            if (!iNetAddress.isLoopbackAddress && !iNetAddress.isLinkLocalAddress) {
                                if (iNetAddress.hostAddress.contains(".")) {
                                    log(iNetAddress.hostAddress)
                                }
                            }
                        }
                    }
                } catch (ex: SocketException) {
                    ex.printStackTrace()
                }
            }
        }
    }

    /**
     * see[userLog]记录
     */
    private fun log(ip: String) {
        viewModelScope.launch {
            getUser()?.let { user ->
                // 类型1:老师,2:学生
                // 类型,1:登录,2:退出
                val param = HashMap<String, String>()
                param["UserType"] = "2"
                param["LoginName"] = user.LoginName
                param["UserId"] = user.ID.toString()
                param["Name"] = user.Name
                param["Version"] = appInfo.versionName()
                param["DevTypeName"] = Build.MODEL
                param["IP"] = ip
                param["Type"] = "1"
                param["Common"] = "学生进入主页"
                param["ClientType"] = "学生-Android-新"
                toolRepo.userLog(param)
            }
        }
    }
}