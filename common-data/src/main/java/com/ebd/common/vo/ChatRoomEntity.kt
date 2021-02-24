package com.ebd.common.vo

import com.chad.library.adapter.base.entity.MultiItemEntity

class ChatRoomEntity(override val itemType: Int) : MultiItemEntity {
    companion object {
        const val TEXT = 1
        const val IMG = 2
        const val AUDIO = 3
    }

    var content: String = ""
    var imgSrc: String = ""
    var avatar: String = ""
    var nick: String = ""
    var account: String = ""
    var fileUrl: String = ""
    var audioDuration: Long = 0
    var isMyself = false

    constructor(mType: Int, mAccount: String, mContent: String, mNick: String, isMyself: Boolean = false) : this(mType) {
        this.isMyself = isMyself
        this.nick = mNick
        this.account = mAccount
        when (itemType) {
            TEXT -> {
                this.content = mContent
            }
            IMG -> {
                this.imgSrc = mContent
            }
        }
    }

    constructor(mType: Int, mAccount: String, mContent: String, avatar: String, mNick: String, isMyself: Boolean = false) : this(mType) {
        this.isMyself = isMyself
        this.avatar = avatar
        this.nick = mNick
        this.account = mAccount
        when (itemType) {
            TEXT -> {
                this.content = mContent
            }
            IMG -> {
                this.imgSrc = mContent
            }
        }
    }

    constructor(mType: Int, mAccount: String, fileUrl: String, audioDuration: Long, avatar: String, mNick: String, isMyself: Boolean = false) : this(mType) {
        this.isMyself = isMyself
        this.avatar = avatar
        this.nick = mNick
        this.account = mAccount
        this.fileUrl = fileUrl
        this.audioDuration = audioDuration
    }

    constructor(mType: Int, mAccount: String, fileUrl: String, audioDuration: Long, mNick: String, isMyself: Boolean = false) : this(mType) {
        this.isMyself = isMyself
        this.nick = mNick
        this.account = mAccount
        this.fileUrl = fileUrl
        this.audioDuration = audioDuration
    }
}