package com.simplepro.todoandmemooffline.Dao

import androidx.room.*
import com.simplepro.todoandmemooffline.instance.DoneTodoInstance

@Dao
interface DoneTodoDao {
    @Query("SELECT * from doneTodo")
    fun getAll() : List<DoneTodoInstance>

    @Insert
    fun insert(doneTodo : DoneTodoInstance)

    @Update
    fun update(doneTodo : DoneTodoInstance)

    @Delete
    fun delete(doneTodo : DoneTodoInstance)
}