package com.ebd.common.cache

import com.ebd.common.App
import com.ebd.common.config.CacheKey
import com.ebd.common.vo.Grade
import com.ebd.common.vo.User

/**
 * 点点作业。轻量级缓存
 */

fun saveUser(user: User, account: String, password: String) {
    CacheTool.setValue(CacheKey.USER to App.instance.globalGson.toJson(user))
    CacheTool.setValue(CacheKey.ACCOUNT to account)
    CacheTool.setValue(CacheKey.PASSWORD to password)
    CacheTool.setValue(CacheKey.USER_ID to user.ID)
}

fun getUser(): User? {
    return try {
        val userJson = CacheTool.getValue(CacheKey.USER, "")
        App.instance.globalGson.fromJson(userJson, User::class.java)
    } catch (e: Exception) {
        null
    }
}

fun updateUserAvatar(path: String) {
    getUser()?.apply {
        Avatar = path
        CacheTool.setValue(CacheKey.USER to App.instance.globalGson.toJson(this))
    }
}

fun updateUserPhone(tel: String) {
    getUser()?.apply {
        Phone = tel
        CacheTool.setValue(CacheKey.USER to App.instance.globalGson.toJson(this))
    }
}

fun getUserName(): String {
    return getUser()?.Name ?: ""
}

fun getSubject(): ArrayList<User.Subject> {
    return try {
        getUser()?.SubjectList?.filter { it.Status == 0 } as ArrayList
    } catch (e: Exception) {
        ArrayList()
    }
}

fun getGrade(): ArrayList<Grade> {
    val list = ArrayList<Grade>()
    list.add(Grade(null, "全部"))
    list.add(Grade(3, "九年级"))
    list.add(Grade(2, "八年级"))
    list.add(Grade(1, "七年级"))
    list.add(Grade(9, "六年级"))
    list.add(Grade(8, "五年级"))
    list.add(Grade(7, "四年级"))
    list.add(Grade(6, "三年级"))
    list.add(Grade(5, "二年级"))
    list.add(Grade(4, "一年级"))
    return list
}

val loginId get() = CacheTool.getValue(CacheKey.USER_ID, 0)
val loginName get() = getUserName()
val password get() = CacheTool.getValue(CacheKey.PASSWORD, "")

fun isLogin(): Boolean {
    return loginId != 0
}