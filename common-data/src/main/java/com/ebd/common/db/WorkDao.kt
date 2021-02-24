package com.ebd.common.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ebd.common.vo.local.MyAnswer

/**
 * 做作业、本地数据库
 */
@Dao
interface WorkDao {

    /**
     * 查询学生本地答案
     * @param id 服务器生成的唯一ID
     */
    @Query("SELECT * FROM MyAnswer WHERE id =:id")
    suspend fun queryMyAnswer(id: Long): MyAnswer?

    /**
     * 插入学生本地答案
     * @param myAnswer 学生答案
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMyAnswer(myAnswer: MyAnswer)

    /**
     * 删除学生本地答案
     * @param id 服务器生成的唯一ID
     */
    @Query("DELETE FROM MyAnswer WHERE id =:id")
    suspend fun deleteMyAnswerById(id: Long)

}