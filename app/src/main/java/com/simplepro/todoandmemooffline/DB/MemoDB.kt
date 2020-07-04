package com.simplepro.todoandmemooffline.DB

import androidx.room.Database
import androidx.room.RoomDatabase
import com.simplepro.secondtodoandmemo.instance.MemoInstance
import com.simplepro.todoandmemooffline.Dao.MemoDao

@Database(entities = [MemoInstance::class], version = 1)
abstract class MemoDB : RoomDatabase() {
    abstract fun memoDao() : MemoDao
}