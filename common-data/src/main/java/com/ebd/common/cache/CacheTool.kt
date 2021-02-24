package com.ebd.common.cache

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.core.content.edit
import com.ebd.common.App
import com.ebd.common.config.Ebd

object CacheTool {

    private val mPreferences by lazy {
        App.instance.getSharedPreferences(Ebd.FILE_NAME, Context.MODE_PRIVATE)
    }

    fun setValue(param: Pair<String, Any?>) {
        mPreferences.edit {
            when (param.second) {
                is Int -> putInt(param.first, param.second as Int)
                is Long -> putLong(param.first, param.second as Long)
                is Float -> putFloat(param.first, param.second as Float)
                is String -> putString(param.first, param.second as String)
                is Boolean -> putBoolean(param.first, param.second as Boolean)
                else -> throw IllegalArgumentException("This type can't be saved into Preferences")
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getValue(key: String, default: T): T {
        return with(mPreferences) {
            when (default) {
                is Int -> getInt(key, default) as T
                is Long -> getLong(key, default) as T
                is Float -> getFloat(key, default) as T
                is String -> getString(key, default) as T
                is Boolean -> getBoolean(key, default) as T
                else -> throw IllegalArgumentException("This type can't be saved into Preferences")
            }
        }
    }

    /**
     * 具有相同sharedId,且签名相同，可跨应用访问数据
     * 复杂数据请使用ContentProvider
     * @return 跨应用的SharedPreferences
     */
    fun getSharedPreference(context: Context, pkgName: String): SharedPreferences? {
        return try {
            val ctx = context.createPackageContext(
                pkgName,
                Context.CONTEXT_IGNORE_SECURITY or Context.CONTEXT_INCLUDE_CODE
            )
            ctx.getSharedPreferences(Ebd.FILE_NAME, Context.MODE_PRIVATE)
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
    }
}




