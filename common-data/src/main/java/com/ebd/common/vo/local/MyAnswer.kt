package com.ebd.common.vo.local

import android.util.SparseArray
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MyAnswer(
    @PrimaryKey val id: Long,
    @ColumnInfo val sparseArray: SparseArray<String?>
)