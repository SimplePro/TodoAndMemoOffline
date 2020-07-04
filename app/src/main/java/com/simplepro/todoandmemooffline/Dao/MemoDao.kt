package com.simplepro.todoandmemooffline.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.simplepro.secondtodoandmemo.instance.MemoInstance

@Dao
interface MemoDao {
    //
    @Query("SELECT * FROM memo")
    fun getAll() : List<MemoInstance>

    @Insert
    fun insert(memo: MemoInstance)

    @Query("DELETE from memo")
    fun deleteAll()
}