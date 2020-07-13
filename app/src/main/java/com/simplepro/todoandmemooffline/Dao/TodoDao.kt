package com.simplepro.todoandmemooffline.Dao

import androidx.room.*
import com.simplepro.todoandmemooffline.instance.TodoInstance

@Dao
interface TodoDao {
    @Query("SELECT * FROM todo ORDER BY todoId DESC")
    fun getAll() : List<TodoInstance>

    @Insert
    fun insert(todo : TodoInstance)

    @Update
    fun update(todo : TodoInstance)

    @Delete
    fun delete(todo : TodoInstance)
}