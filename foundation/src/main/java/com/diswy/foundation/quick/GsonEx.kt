package com.diswy.foundation.quick

import com.google.gson.Gson
import java.util.*

/**
 * Gson解析扩展
 * Created by @author xiaofu on 2019/3/25.
 */
inline fun <reified T> Gson.fromJsonArray(json: String, clazz: Class<T>): ArrayList<T> {
    val listType = ParameterizedTypeImpl(ArrayList::class.java, arrayOf<Class<*>>(clazz))
    return this.fromJson(json, listType)
}