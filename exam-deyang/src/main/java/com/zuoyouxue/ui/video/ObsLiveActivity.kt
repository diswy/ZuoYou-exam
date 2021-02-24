package com.zuoyouxue.ui.video

import android.annotation.SuppressLint
import android.graphics.Color
import android.text.TextUtils
import android.util.Log
import android.view.WindowManager
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.diswy.foundation.base.Permission
import com.diswy.foundation.base.activity.BaseBindActivity
import com.diswy.foundation.quick.toast
import com.diswy.foundation.web.Resource
import com.diswy.foundation.web.Status
import com.ebd.common.App
import com.ebd.common.Page
import com.ebd.common.cache.getUser
import com.ebd.common.cache.isLogin
import com.ebd.common.cache.password
import com.ebd.common.viewmodel.VideoViewModel
import com.ebd.common.vo.LiveAdd
import com.ebd.common.vo.LiveInfo
import com.ebd.common.vo.local.ObsLiveItem
import com.google.android.material.tabs.TabLayoutMediator
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.RequestCallback
import com.netease.nimlib.sdk.ResponseCode
import com.netease.nimlib.sdk.auth.AuthService
import com.netease.nimlib.sdk.auth.LoginInfo
import com.netease.nimlib.sdk.chatroom.ChatRoomService
import com.netease.nimlib.sdk.chatroom.model.EnterChatRoomData
import com.netease.nimlib.sdk.chatroom.model.EnterChatRoomResultData
import com.zuoyouxue.R
import com.zuoyouxue.adapter.ObsLiveAdapter
import com.zuoyouxue.databinding.ActivityObsLiveBinding
import com.zuoyouxue.tool.MD5
import kit.diswy.player.PlayerEvent
import pub.devrel.easypermissions.AfterPermissionGranted

@Route(path = Page.VIDEO_OBS_LIVE)
class ObsLiveActivity : BaseBindActivity<ActivityObsLiveBinding>(), PlayerEvent {

    @Autowired
    @JvmField
    var periodId: Int = 0

    private var mChatRoomId: String? = null// 房间ID

    private val titleList = listOf("提问/解答", "白板")

    private val videoViewModel: VideoViewModel by viewModels { App.instance.factory }

    private val infoObserver = Observer<Resource<LiveInfo>> {
        when (it.status) {
            Status.LOADING -> {
            }
            Status.SUCCESS -> {
                play(it.data!!.ChannelPullUrls)
                mChatRoomId = it.data!!.ChatRoomId
                enterRoom(mChatRoomId!!)
                initViewPager(mChatRoomId!!)
            }
            Status.ERROR -> handleExceptions(it.throwable)
        }
    }

    override fun keepScreenOn(): Boolean = true

    override fun emptyBackground(): Boolean = true

    override fun setView() {
        window.statusBarColor = Color.BLACK
        super.setView()
    }

    override fun getLayoutRes(): Int = R.layout.activity_obs_live

    override fun initialize() {
        loginNetease()  // 首先登录网易云信
    }

    /**
     * 登录网易云信
     */
    private fun loginNetease() {
        getUser()?.let {
            if (!TextUtils.isEmpty(password) && isLogin()) {
                val mAccount = "student_${it.ID}"
                val token = MD5.getStringMD5(password)
                NIMClient.getService(AuthService::class.java).login(LoginInfo(mAccount, token))
                    .setCallback(object : RequestCallback<LoginInfo> {
                        @SuppressLint("LogNotTimber")
                        override fun onSuccess(param: LoginInfo) {
                            Log.d(
                                "登录网易云信 onSuccess",
                                "网易云信:account = ${param.account} ; psd = ${param.token} ; " +
                                        "appKey = ${param.appKey} "
                            )
                            startPageView()
                        }

                        @SuppressLint("LogNotTimber")
                        override fun onFailed(code: Int) {
                            if (code == 302 || code == 404) {
                                Log.e("登录网易云信 onFailed", "账号或密码错误")
                            } else {
                                Log.e("登录网易云信 onFailed", "登录失败: $code")
                            }
                            toastLivePage()
                        }

                        @SuppressLint("LogNotTimber")
                        override fun onException(exception: Throwable) {
                            Log.e("登录网易云信 onException:", exception.message)
                            toastLivePage()
                        }
                    })
            }
        }
    }

    private fun toastLivePage() {
        toast("直播登录失败，请退出当前页面重新尝试")
    }

    private fun startPageView() {
        if (hasRecordPermission()) {
            initView()
        }
    }

    @AfterPermissionGranted(Permission.RC_RECORD_PERM)
    private fun initView() {
        if (hasRecordPermission()) {
            lifecycle.addObserver(binding.livePlayer)
            binding.livePlayer.addEventListener(this)
            binding.liveTabLayout.run {
                addTab(newTab())
            }
            videoViewModel.liveInfo.observe(this, infoObserver)
            videoViewModel.getLiveInfo(periodId)
        } else {
            finish()
        }
    }

    /**
     * 初始化ViewPager
     */
    private fun initViewPager(chatRoomId: String) {
        val obsLiveItemList = listOf(
            ObsLiveItem(chatRoomId, Page.CHAT_ROOM)
        )
        binding.liveViewPager.adapter = ObsLiveAdapter(this, obsLiveItemList)
        TabLayoutMediator(binding.liveTabLayout, binding.liveViewPager,
            TabLayoutMediator.TabConfigurationStrategy { tab, position ->
                tab.text = titleList[position]
            }).attach()
    }

    override fun isFull(isFull: Boolean) {
        toggleScreen(isFull)
    }

    override fun playerBack() {
        onBackPressed()
    }

    override fun onBackPressed() {
        if (binding.livePlayer.isFullScreen()) {
            binding.livePlayer.setFullScreen(false)
            toggleScreen(false)
        } else {
            super.onBackPressed()
        }
    }

    private fun play(add: String) {
        try {
            val addData: LiveAdd = App.instance.globalGson.fromJson(add, LiveAdd::class.java)
            binding.livePlayer.play(this, addData.rtmpPullUrl)
        } catch (e: Exception) {
            handleExceptions(e)
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private fun toggleScreen(isFull: Boolean) {
        val lp = binding.livePlayer.layoutParams as LinearLayout.LayoutParams
        if (isFull) {
            lp.height = LinearLayout.LayoutParams.MATCH_PARENT
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )

        } else {
            lp.height = resources.getDimensionPixelOffset(R.dimen.player_height)
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
        binding.livePlayer.layoutParams = lp
    }

    private fun enterRoom(roomId: String) {
        if (TextUtils.isEmpty(roomId)) {
            toast("进入聊天室失败，请退出当前页面重新尝试")
            return
        }
        val data = EnterChatRoomData(roomId)
        data.nick = getUser()?.Name
        data.avatar = getUser()?.Avatar
        NIMClient.getService(ChatRoomService::class.java).enterChatRoomEx(data, 3)
            .setCallback(object : RequestCallback<EnterChatRoomResultData> {
                override fun onSuccess(result: EnterChatRoomResultData) {
                    toast("您已进入${result.roomInfo.name}聊天室")
                }

                override fun onFailed(code: Int) {
                    when (code) {
                        ResponseCode.RES_CHATROOM_BLACKLIST.toInt() -> toast("你已被拉入黑名单，不能再进入聊天室")
                        ResponseCode.RES_ENONEXIST.toInt() -> toast("该聊天室不存在")
                        else -> toast("无法使用聊天室功能，请联系管理员。错误码：$code")
                    }
                }

                override fun onException(exception: Throwable) {
                    toast("进入聊天室失败，请退出当前页面重新尝试")
                }
            })
    }

    override fun onDestroy() {
        exitRoom()
        super.onDestroy()
    }

    private fun exitRoom() {
        mChatRoomId?.let {
            NIMClient.getService(ChatRoomService::class.java).exitChatRoom(it)
        }
    }

}
