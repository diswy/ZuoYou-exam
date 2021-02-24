package com.zuoyouxue.ui.video.tab

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Environment
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.diswy.foundation.base.Permission
import com.diswy.foundation.base.fragment.BaseBindFragment
import com.diswy.foundation.quick.dip
import com.diswy.foundation.quick.toast
import com.ebd.common.Page
import com.ebd.common.cache.getUser
import com.ebd.common.config.DataKey
import com.ebd.common.vo.ChatRoomEntity
import com.netease.nimlib.sdk.*
import com.netease.nimlib.sdk.Observer
import com.netease.nimlib.sdk.chatroom.ChatRoomMessageBuilder
import com.netease.nimlib.sdk.chatroom.ChatRoomService
import com.netease.nimlib.sdk.chatroom.ChatRoomServiceObserver
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessage
import com.netease.nimlib.sdk.chatroom.model.ChatRoomStatusChangeData
import com.netease.nimlib.sdk.media.record.AudioRecorder
import com.netease.nimlib.sdk.media.record.IAudioRecordCallback
import com.netease.nimlib.sdk.media.record.RecordType
import com.netease.nimlib.sdk.msg.MsgServiceObserve
import com.netease.nimlib.sdk.msg.attachment.AudioAttachment
import com.netease.nimlib.sdk.msg.attachment.ImageAttachment
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum
import com.netease.nimlib.sdk.msg.model.IMMessage
import com.yalantis.ucrop.UCrop
import com.zuoyouxue.R
import com.zuoyouxue.adapter.ChatRoomMultipleAdapter
import com.zuoyouxue.databinding.FragmentLiveChatBinding
import com.zuoyouxue.ui.ImagePreviewActivity
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kit.diswy.photo.singleImagePicker
import kit.diswy.player.ExAudioPlayer
import kotlinx.android.synthetic.main.fragment_live_chat.*
import pub.devrel.easypermissions.AfterPermissionGranted
import top.zibin.luban.Luban
import top.zibin.luban.OnCompressListener
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.HashMap

/**
 * 提问与解答互动直播
 */
@Route(path = Page.CHAT_ROOM)
class LiveChatFragment : BaseBindFragment<FragmentLiveChatBinding>() {

    companion object {
        const val TAG: String = "LiveChatFragment"
    }

    @Autowired
    @JvmField
    var chatRoomId: String? = null
    private lateinit var mAnimation: AnimationDrawable
    private var tempAnimation: AnimationDrawable? = null
    private var mDisposable: Disposable? = null// 倒计时专用

    private fun timer() {
        mDisposable?.dispose()
        mDisposable = Flowable.intervalRange(0, 60, 0, 1, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                val mySecond = (60 - it - 1).toInt()
                if (mySecond <= 5) {
                    binding.tvRecordHint.text = "最多还可以录制${mySecond}秒"
                }
            }
            .doOnComplete {
                timerCancel()
                binding.btnSpeak.text = "按住 说话"
                stopAudio(false)
                mAnimation.stop()
                binding.icMic.visibility = View.GONE
                binding.btnSpeak.setBackgroundColor(
                    ContextCompat.getColor(
                        requireActivity(),
                        R.color.white
                    )
                )
            }.subscribe()
    }

    private fun timerCancel() {
        mDisposable?.dispose()
        binding.tvRecordHint.text = "手指上滑，取消发送"
    }

    private val mAdapter = ChatRoomMultipleAdapter()

    override fun getLayoutRes(): Int {
        return R.layout.fragment_live_chat
    }

    override fun initialize() {
        registerObservers(true)
        binding.mChatRoomRv.adapter = mAdapter
        mAnimation = binding.ivRecordingAnimation.drawable as AnimationDrawable
    }

    override fun onDestroyView() {
        mExoAudioPlayer.setOnCompletionListener(null)
        mExoAudioPlayer.release()
        registerObservers(false)
        super.onDestroyView()
    }


    private var mPosY = 0f
    private var mEndPosY = 0f

    @SuppressLint("ClickableViewAccessibility")
    override fun bindListener() {
        binding.mSwitchInputType.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                buttonView.setBackgroundResource(R.drawable.ic_btn_keyboard)
                binding.btnSpeak.visibility = View.VISIBLE
            } else {
                buttonView.setBackgroundResource(R.drawable.ic_btn_mic)
                binding.btnSpeak.visibility = View.GONE
            }
        }

        binding.btnSpeak.setOnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    mPosY = motionEvent.rawY
                    binding.btnSpeak.text = "松开 结束"
                    binding.icMic.visibility = View.VISIBLE
                    mAnimation.start()
                    binding.btnSpeak.setBackgroundColor(
                        ContextCompat.getColor(
                            requireActivity(),
                            R.color.color_cd
                        )
                    )
                    startAudio()
                }
                MotionEvent.ACTION_UP -> {
                    timerCancel()
                    binding.btnSpeak.text = "按住 说话"
                    val gap = mPosY - mEndPosY
                    if (gap > 100) {// 认为上滑取消
                        stopAudio(true)
                    } else { // 发送
                        stopAudio(false)
                    }
                    mAnimation.stop()
                    binding.icMic.visibility = View.GONE
                    binding.btnSpeak.setBackgroundColor(
                        ContextCompat.getColor(
                            requireActivity(),
                            R.color.white
                        )
                    )
                }

                MotionEvent.ACTION_MOVE -> {
                    mEndPosY = motionEvent.rawY
                    val gap = mPosY - mEndPosY
                    if (gap > 100) { // 认为上滑取消 刷新UI
                        binding.icMic.setBackgroundResource(R.drawable.bg_mic_cancel)
                        binding.tvRecordHint.text = "松开手指，取消发送"
                        binding.ivMicCancel.visibility = View.VISIBLE
                        binding.ivMicRecording.visibility = View.GONE
                        mAnimation.stop()
                    } else { // 发送
                        binding.icMic.setBackgroundResource(R.drawable.bg_mic)
                        binding.tvRecordHint.text = "手指上滑，取消发送"
                        binding.ivMicCancel.visibility = View.GONE
                        binding.ivMicRecording.visibility = View.VISIBLE
                        mAnimation.start()
                    }
                }
            }
            return@setOnTouchListener true
        }

        binding.mBtnSend.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                val lp = binding.mBtnSend.layoutParams
                lp.width = requireContext().dip(60)
                binding.mBtnSend.layoutParams = lp
                buttonView.setBackgroundResource(R.color.colorPrimary)
            } else {
                val lp = binding.mBtnSend.layoutParams
                lp.width = requireContext().dip(38)
                binding.mBtnSend.layoutParams = lp
                buttonView.setBackgroundResource(R.drawable.ic_btn_pic)
            }
        }


        binding.mBtnSend.setOnClickListener {
            if (binding.mBtnSend.isChecked) { // 发送图片
                startPicker()
            } else {// 发送文本
                sendTextMsg(binding.mChatRoomEdit.text.toString().trim())
                binding.mChatRoomEdit.setText("")
                hideSoftKeyboard(requireContext(), binding.mChatRoomEdit)
            }

            binding.mBtnSend.isChecked = binding.mChatRoomEdit.text.toString().trim().isNotEmpty()
        }

        binding.mChatRoomEdit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                binding.mBtnSend.isChecked = s.toString().trim().isNotEmpty()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        mAdapter.setAudioCallback { url, animation ->
            tempAnimation?.stop()
            tempAnimation?.selectDrawable(0)
            tempAnimation = animation
            if (mExoAudioPlayer.isPlaying()) {
                mExoAudioPlayer.release()
                tempAnimation?.stop()
                tempAnimation?.selectDrawable(0)
            } else {
                mExoAudioPlayer.openAudio(url)
                tempAnimation?.start()
            }
        }

        mAdapter.setPicListener { v, path ->
            val options = ActivityOptionsCompat
                .makeSceneTransitionAnimation(requireActivity(), v, "image_preview")
            val intent = Intent(requireActivity(), ImagePreviewActivity::class.java)
            intent.putExtra(DataKey.ImgUrl, path)
            ActivityCompat.startActivity(requireActivity(), intent, options.toBundle())
        }

        mExoAudioPlayer.setOnCompletionListener {
            tempAnimation?.stop()
            tempAnimation?.selectDrawable(0)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            data?.let {
                val filePath = UCrop.getOutput(data)?.path
                if (filePath != null) {
                    compressImage(filePath)
                    mAdapter.addData(
                        ChatRoomEntity(
                            ChatRoomEntity.IMG, getUser()?.LoginName
                                ?: "未知账号", filePath, getUser()?.Name
                                ?: "神秘同学", true
                        )
                    )
                    mChatRoomRv.scrollToPosition(mAdapter.data.size - 1)
                }
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            toast("发生错误，请重新拍照，或换张图片")
        }
    }

    @AfterPermissionGranted(Permission.RC_STORAGE_PERM)
    private fun startPicker() {
        if (hasStoragePermission()) {
            startPickerCamera()
        }
    }

    @AfterPermissionGranted(Permission.RC_CAMERA_PERM)
    private fun startPickerCamera() {
        if (hasCameraPermission()) {
            singleImagePicker(6)
        }
    }


    fun onCurrent() {
        mChatRoomRv?.apply {
            adapter = mAdapter
            try {
                mChatRoomRv.scrollToPosition(mAdapter.data.size - 1)
            } catch (e: IndexOutOfBoundsException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 注册云信服务监听
     */
    private fun registerObservers(register: Boolean) {
        NIMClient.getService(ChatRoomServiceObserver::class.java)
            .observeOnlineStatus(onlineStatus, register)
        NIMClient.getService(ChatRoomServiceObserver::class.java)
            .observeReceiveMessage(incomingChatRoomMsg, register)
        NIMClient.getService(MsgServiceObserve::class.java)
            .observeMsgStatus(statusObserver, register)
    }

    /**
     * 状态
     */
    private val onlineStatus: Observer<ChatRoomStatusChangeData> =
        Observer { chatRoomStatusChangeData ->
            if (chatRoomStatusChangeData.status == StatusCode.CONNECTING) {
                // DialogMaker.updateLoadingMessage("连接中...");
            } else if (chatRoomStatusChangeData.status == StatusCode.UNLOGIN) {
                if (NIMClient.getService(ChatRoomService::class.java).getEnterErrorCode(chatRoomId)
                    == ResponseCode.RES_CHATROOM_STATUS_EXCEPTION.toInt()
                ) {
                    // 聊天室连接状态异常
                    NIMClient.getService(ChatRoomService::class.java).exitChatRoom(chatRoomId)
                } else {
//                     Toast.makeText(ChatRoomActivity.this, R.string.nim_status_unlogin, Toast.LENGTH_SHORT).show();
                }
            } else if (chatRoomStatusChangeData.status == StatusCode.LOGINING) {
                // DialogMaker.updateLoadingMessage("登录中...");
            } else if (chatRoomStatusChangeData.status == StatusCode.LOGINED) {
            } else if (chatRoomStatusChangeData.status.wontAutoLogin()) {
            } else if (chatRoomStatusChangeData.status == StatusCode.NET_BROKEN) {
            }
        }

    private val incomingChatRoomMsg: Observer<List<ChatRoomMessage>> = Observer { messages ->
        val mMsgSingle = messages[messages.size - 1]
        when (mMsgSingle.msgType) {
            MsgTypeEnum.text -> {// 处理文本消息
                mAdapter.addData(
                    ChatRoomEntity(
                        ChatRoomEntity.TEXT,
                        mMsgSingle.fromAccount,
                        mMsgSingle.content,
                        mMsgSingle.chatRoomMessageExtension.senderAvatar,
                        mMsgSingle.chatRoomMessageExtension.senderNick
                    )
                )
            }
            MsgTypeEnum.image -> {// 处理图片
                val imgSrc = (mMsgSingle.attachment as ImageAttachment).url
                mAdapter.addData(
                    ChatRoomEntity(
                        ChatRoomEntity.IMG,
                        mMsgSingle.fromAccount,
                        imgSrc,
                        mMsgSingle.chatRoomMessageExtension.senderAvatar,
                        mMsgSingle.chatRoomMessageExtension.senderNick
                    )
                )
            }
            MsgTypeEnum.audio -> {
                try {
                    val mAudioAttachment = mMsgSingle.attachment as AudioAttachment
                    mAdapter.addData(
                        ChatRoomEntity(
                            ChatRoomEntity.AUDIO,
                            mMsgSingle.fromAccount,
                            mAudioAttachment.url,
                            mAudioAttachment.duration,
                            mMsgSingle.chatRoomMessageExtension.senderAvatar,
                            mMsgSingle.chatRoomMessageExtension.senderNick
                        )
                    )
                } catch (e: Exception) {

                }
            }
            MsgTypeEnum.notification -> {

            }
            else -> {
            }
        }
        if (mAdapter.data.isNotEmpty() && mChatRoomRv != null) {
            mChatRoomRv.scrollToPosition(mAdapter.data.size - 1)
        }
    }

    private val statusObserver = Observer<IMMessage> { msg ->
        // 1、根据sessionId判断是否是自己的消息
        // 2、更改内存中消息的状态
        // 3、刷新界面
        if (msg.attachment is ImageAttachment) {
//            GlideApp.with(this@TestChatRoomActivity)
//                    .load((msg.attachment as ImageAttachment).path)
//                    .into(iv)
//            Logger.d((msg.attachment as ImageAttachment).path)
        } else {
//            Logger.d("这不属于图片")
        }
    }

    // 创建聊天室文本消息并发送
    private fun sendTextMsg(content: String) {
        val message = ChatRoomMessageBuilder.createChatRoomTextMessage(chatRoomId, content)
        val mExtensionMap = HashMap<String, Any>()
        mExtensionMap["avatar"] = getUser()?.Avatar ?: ""
        mExtensionMap["nickName"] = getUser()?.Name ?: "神秘同学"
        message.remoteExtension = mExtensionMap
        NIMClient.getService(ChatRoomService::class.java).sendMessage(message, false)
            .setCallback(object : RequestCallback<Void> {
                override fun onSuccess(param: Void?) {
                    mAdapter.addData(
                        ChatRoomEntity(
                            ChatRoomEntity.TEXT, getUser()?.LoginName
                                ?: "未知账号", content, getUser()?.Name
                                ?: "神秘同学", true
                        )
                    )
                    if (mAdapter.data.isNotEmpty() && mChatRoomRv != null) {
                        mChatRoomRv.scrollToPosition(mAdapter.data.size - 1)
                    }
                }

                @SuppressLint("LogNotTimber")
                override fun onFailed(code: Int) {
                    Log.e(TAG, "onTextFailed code = $code")
                }

                @SuppressLint("LogNotTimber")
                override fun onException(exception: Throwable?) {
                    Log.e(TAG, "onTextException:${exception?.message}")
                }
            })
    }

    private fun sendAudioMsg(file: File?, length: Long) {
        file ?: return
        val audioMsg = ChatRoomMessageBuilder.createChatRoomAudioMessage(chatRoomId, file, length)
        NIMClient.getService(ChatRoomService::class.java).sendMessage(audioMsg, false)
            .setCallback(object : RequestCallback<Void> {
                override fun onSuccess(param: Void?) {
                    mAdapter.addData(
                        ChatRoomEntity(
                            ChatRoomEntity.AUDIO,
                            getUser()?.LoginName ?: "未知账号",
                            file.absolutePath,
                            length,
                            getUser()?.Name ?: "神秘同学",
                            true
                        )
                    )
                    mChatRoomRv.scrollToPosition(mAdapter.data.size - 1)
                }

                @SuppressLint("LogNotTimber")
                override fun onFailed(code: Int) {
                    Log.e(TAG, "onTextFailed code = $code")
                }

                @SuppressLint("LogNotTimber")
                override fun onException(exception: Throwable?) {
                    Log.e(TAG, "onTextException:${exception?.message}")
                }

            })
    }

    private fun startAudio() {
        activity ?: return
        mAudioRecorder.startRecord()
    }

    //如果cancel为true，回调onRecordCancel, 为false，回调onRecordSuccess
    private fun stopAudio(cancel: Boolean) {
        mAudioRecorder.completeRecord(cancel)
    }

    private val mAudioRecorder: AudioRecorder by lazy {
        AudioRecorder(activity, RecordType.AAC, 61, audioCallBack)
    }
    private val mExoAudioPlayer by lazy { ExAudioPlayer(requireActivity()) }

    // 音频录制回调
    private val audioCallBack = object : IAudioRecordCallback {
        @SuppressLint("LogNotTimber")
        override fun onRecordSuccess(audioFile: File?, audioLength: Long, recordType: RecordType?) {
            Log.e(TAG, "onRecordSuccess")
            sendAudioMsg(audioFile, audioLength)
        }

        @SuppressLint("LogNotTimber")
        override fun onRecordReachedMaxTime(maxTime: Int) {
            Log.e(TAG, "onRecordReachedMaxTime maxTime = $maxTime")
        }

        @SuppressLint("LogNotTimber")
        override fun onRecordReady() {
            Log.e(TAG, "onRecordReady")
            timer()
        }

        @SuppressLint("LogNotTimber")
        override fun onRecordCancel() {
            Log.e(TAG, "onRecordCancel")
        }

        @SuppressLint("LogNotTimber")
        override fun onRecordStart(audioFile: File?, recordType: RecordType?) {
            if (audioFile != null) {
                Log.e(TAG, "onRecordStart audioFile = ${audioFile.absoluteFile}")
            }
        }

        @SuppressLint("LogNotTimber")
        override fun onRecordFail() {
            Log.e(TAG, "onRecordFail")
        }
    }

    /**
     * 创建图片消息并发送
     *
     * @file 压缩处理一下文件，避免图过大
     */
    private fun sendImgMsg(file: File) {
        val message = ChatRoomMessageBuilder.createChatRoomImageMessage(chatRoomId, file, file.name)
        val mExtensionMap = HashMap<String, Any>()
        mExtensionMap["avatar"] = getUser()?.Avatar ?: ""
        mExtensionMap["nickName"] = getUser()?.Name ?: "神秘同学"
        message.remoteExtension = mExtensionMap
        NIMClient.getService(ChatRoomService::class.java).sendMessage(message, false)
            .setCallback(object : RequestCallback<Void> {
                @SuppressLint("LogNotTimber")
                override fun onSuccess(param: Void?) {
                    Log.e(TAG, "onSuccess Pic")
                }

                @SuppressLint("LogNotTimber")
                override fun onFailed(code: Int) {
                    Log.e(TAG, "onFailed Pic, code = $code")
                }

                @SuppressLint("LogNotTimber")
                override fun onException(exception: Throwable?) {
                    Log.e(TAG, "onException Pic:${exception?.message}")
                }
            })
    }

    /**
     * 压缩图片
     */
    private fun compressImage(path: String) {
        Luban.with(context)
            .load(path)
            .ignoreBy(256)
            .setTargetDir(getPath())
            .filter {
                return@filter !(TextUtils.isEmpty(it) || it.toLowerCase(Locale.ROOT).endsWith(".gif"))
            }
            .setCompressListener(object : OnCompressListener {
                override fun onSuccess(file: File?) {
                    file?.let {
                        sendImgMsg(it)
                    }
                    if (file == null) {
                        toast("图片发送失败，请重新尝试")
                    }
                }

                @SuppressLint("LogNotTimber")
                override fun onError(e: Throwable?) {
                    toast("图片发送失败，请重新尝试")
                    Log.e(TAG, "${e?.message}")
                }

                override fun onStart() {
                }
            })
            .launch()
    }

    private fun getPath(): String {
        val path = "${Environment.getExternalStorageDirectory()}/cqebd/image/"
        val file = File(path)
        if (file.mkdirs()) {
            return path
        }
        return path
    }
}