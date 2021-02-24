package com.zuoyouxue.adapter

import android.graphics.drawable.AnimationDrawable
import android.view.Gravity
import android.view.View
import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.diswy.foundation.tools.GlideApp
import com.ebd.common.cache.getUser
import com.ebd.common.vo.ChatRoomEntity
import com.zuoyouxue.R
import kotlinx.android.synthetic.main.item_chat_room_audio.view.*
import kotlinx.android.synthetic.main.item_chat_room_img.view.*
import kotlinx.android.synthetic.main.item_chat_room_text.view.*
import java.util.*

class ChatRoomMultipleAdapter : BaseMultiItemQuickAdapter<ChatRoomEntity, BaseViewHolder>() {

    init {
        addItemType(ChatRoomEntity.TEXT, R.layout.item_chat_room_text)
        addItemType(ChatRoomEntity.IMG, R.layout.item_chat_room_img)
        addItemType(ChatRoomEntity.AUDIO, R.layout.item_chat_room_audio)
    }

    private var mAudioCallback: ((path: String, animator: AnimationDrawable) -> Unit)? = null

    fun setAudioCallback(listener: (path: String, animator: AnimationDrawable) -> Unit) {
        this.mAudioCallback = listener
    }

    private var mPicCallback: ((view: View, path: String) -> Unit)? = null

    fun setPicListener(listener: (view: View, path: String) -> Unit) {
        this.mPicCallback = listener
    }

    override fun convert(helper: BaseViewHolder, item: ChatRoomEntity) {
        when (helper.itemViewType) {
            ChatRoomEntity.TEXT -> {
                helper.itemView.apply {
                    if (item.account.toLowerCase(Locale.getDefault()).contains("teacher")) { // 老师消息
                        mOtherAvatar.visibility = if (item.isMyself) View.GONE else View.VISIBLE
                        mMyAvatar.visibility = if (item.isMyself) View.VISIBLE else View.GONE
                        mStartSpace.visibility = if (item.isMyself) View.VISIBLE else View.GONE
                        mEndSpace.visibility = if (item.isMyself) View.GONE else View.VISIBLE
                        mChatRoomContent.text = item.content
                        mChatRoomContent.setTextColor(
                            if (item.isMyself) ContextCompat.getColor(
                                context, R.color.white
                            ) else ContextCompat.getColor(context, R.color.red)
                        )
                        mChatRoomContent.setBackgroundResource(
                            if (item.isMyself) R.drawable.chat_bg_me else R.drawable.chat_bg_others
                        )
                        mChatRoomNick.text = "老师"

                        GlideApp.with(context)
                            .asBitmap()
                            .circleCrop()
                            .load(R.drawable.avtar_teacher)
                            .into(if (item.isMyself) mMyAvatar else mOtherAvatar)
                    } else {
                        mOtherAvatar.visibility = if (item.isMyself) View.GONE else View.VISIBLE
                        mMyAvatar.visibility = if (item.isMyself) View.VISIBLE else View.GONE
                        mStartSpace.visibility = if (item.isMyself) View.VISIBLE else View.GONE
                        mEndSpace.visibility = if (item.isMyself) View.GONE else View.VISIBLE
                        mChatRoomContent.text = item.content

                        mChatRoomContent.setTextColor(
                            if (item.isMyself) ContextCompat.getColor(
                                context, R.color.white
                            ) else ContextCompat.getColor(context, R.color.black)
                        )
                        mChatRoomContent.setBackgroundResource(
                            if (item.isMyself) R.drawable.chat_bg_me else R.drawable.chat_bg_others
                        )

                        mChatRoomNick.text = item.nick
                        mChatRoomNick.gravity = if (item.isMyself) Gravity.END else Gravity.START
                        GlideApp.with(context)
                            .asBitmap()
                            .circleCrop()
                            .placeholder(R.drawable.ic_avatar)
                            .load(if (item.isMyself) getUser()?.Avatar else item.avatar)
                            .into(if (item.isMyself) mMyAvatar else mOtherAvatar)
                    }
                }
            }
            ChatRoomEntity.IMG -> {
                helper.itemView.apply {
                    if (item.account.toLowerCase(Locale.getDefault()).contains("teacher")) {// 老师消息
                        mImgOtherAvatar.visibility = if (item.isMyself) View.GONE else View.VISIBLE
                        mImgMyAvatar.visibility = if (item.isMyself) View.VISIBLE else View.GONE
                        mImgStartSpace.visibility = if (item.isMyself) View.VISIBLE else View.GONE
                        mImgEndSpace.visibility = if (item.isMyself) View.GONE else View.VISIBLE
                        mChatRoomNick2.text = "老师"
                        mChatRoomNick2.gravity = if (item.isMyself) Gravity.END else Gravity.START

                        GlideApp.with(context)
                            .load(item.imgSrc)
                            .fitCenter()
                            .into(mImgIv)

                        GlideApp.with(context)
                            .asBitmap()
                            .circleCrop()
                            .load(R.drawable.avtar_teacher)
                            .into(if (item.isMyself) mImgMyAvatar else mImgOtherAvatar)
                    } else {
                        mImgOtherAvatar.visibility = if (item.isMyself) View.GONE else View.VISIBLE
                        mImgMyAvatar.visibility = if (item.isMyself) View.VISIBLE else View.GONE
                        mImgStartSpace.visibility = if (item.isMyself) View.VISIBLE else View.GONE
                        mImgEndSpace.visibility = if (item.isMyself) View.GONE else View.VISIBLE
                        mChatRoomNick2.text = item.nick

                        GlideApp.with(context)
                            .load(item.imgSrc)
                            .fitCenter()
                            .into(mImgIv)

                        GlideApp.with(context)
                            .asBitmap()
                            .circleCrop()
                            .placeholder(R.drawable.ic_avatar)
                            .load(if (item.isMyself) getUser()?.Avatar else item.avatar)
                            .into(if (item.isMyself) mImgMyAvatar else mImgOtherAvatar)
                    }

                    mImgIv.setOnClickListener {
                        mPicCallback?.invoke(it, item.imgSrc)
                    }

                }
            }
            ChatRoomEntity.AUDIO -> {
                helper.itemView.apply {
                    mAudioOtherAvatar.visibility = if (item.isMyself) View.GONE else View.VISIBLE
                    mAudioMyAvatar.visibility = if (item.isMyself) View.VISIBLE else View.GONE
                    mAudioStartSpace.visibility = if (item.isMyself) View.VISIBLE else View.GONE
                    mAudioEndSpace.visibility = if (item.isMyself) View.GONE else View.VISIBLE
                    mAudioChatRoomNick.text = item.nick
                    mAudioChatRoomNick.gravity = if (item.isMyself) Gravity.END else Gravity.START

                    mAudioPlay.setBackgroundResource(if (item.isMyself) R.drawable.chat_bg_me else R.drawable.chat_bg_others)
                    tv_duration.setTextColor(
                        if (item.isMyself) ContextCompat.getColor(context, R.color.white)
                        else ContextCompat.getColor(context, R.color.colorPrimary)
                    )
                    iv_voice.setImageDrawable(
                        if (item.isMyself) ContextCompat.getDrawable(
                            context,
                            R.drawable.animation_inner_voice
                        )
                        else ContextCompat.getDrawable(
                            context,
                            R.drawable.animation_inner_other_voice
                        )
                    )

                    GlideApp.with(context)
                        .asBitmap()
                        .circleCrop()
                        .placeholder(R.drawable.ic_avatar)
                        .load(if (item.isMyself) getUser()?.Avatar else item.avatar)
                        .into(if (item.isMyself) mAudioMyAvatar else mAudioOtherAvatar)

                    tv_duration.text = "${(item.audioDuration / 1000).toInt()}"

                    val animator = iv_voice.drawable as AnimationDrawable

                    mAudioPlay.setOnClickListener {
                        mAudioCallback?.invoke(item.fileUrl, animator)
                    }
                }
            }
        }
    }
}