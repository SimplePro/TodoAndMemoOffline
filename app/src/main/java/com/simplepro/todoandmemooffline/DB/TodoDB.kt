package com.simplepro.todoandmemooffline.DB

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.simplepro.todoandmemooffline.Dao.TodoDao
import com.simplepro.todoandmemooffline.instance.TodoInstance

@Database(entities = [TodoInstance::class], version = 1)
abstract class TodoDB : RoomDatabase() {
    abstract fun todoDao() : TodoDao
}