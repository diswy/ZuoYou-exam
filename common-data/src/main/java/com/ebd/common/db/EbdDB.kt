package com.ebd.common.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ebd.common.vo.local.MyAnswer

@Database(
    entities = [MyAnswer::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converter::class)
abstract class EbdDB : RoomDatabase() {
    abstract fun workDao(): WorkDao
}