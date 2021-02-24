package com.ebd.common.db

import android.util.SparseArray
import androidx.room.TypeConverter
import com.ebd.common.App
import com.google.gson.reflect.TypeToken

class Converter {

    @TypeConverter
    fun fromSparseArray(map: SparseArray<String?>): String {
        return App.instance.globalGson.toJson(map)
    }

    @TypeConverter
    fun toSparseArray(mapString: String): SparseArray<String?> {
        val type = object : TypeToken<SparseArray<String?>>() {}.type
        return App.instance.globalGson.fromJson(mapString, type)
    }
}