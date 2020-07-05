package com.simplepro.todoandmemooffline.Dao

import androidx.room.*
import com.simplepro.secondtodoandmemo.instance.MemoInstance

@Dao
interface MemoDao {
    @Query("SELECT * FROM memo")
    fun getAll() : List<MemoInstance>

    @Insert
    fun insert(memo: MemoInstance)

    @Update
    fun update(memo: MemoInstance)

    @Delete
    fun delete(memo: MemoInstance)
}