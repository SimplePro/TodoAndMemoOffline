package com.simplepro.todoandmemooffline.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.simplepro.secondtodoandmemo.instance.TodoInstance

@Dao
interface TodoDao {//
    @Query("SELECT * FROM todo")
    fun getAll() : List<TodoInstance>

    @Insert
    fun insert(todo : TodoInstance)

    @Query("DELETE from todo")
    fun deleteAll()
}