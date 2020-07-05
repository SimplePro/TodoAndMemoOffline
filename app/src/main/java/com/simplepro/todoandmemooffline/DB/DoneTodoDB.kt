package com.simplepro.todoandmemooffline.DB

import androidx.room.Database
import androidx.room.Entity
import androidx.room.RoomDatabase
import com.simplepro.todoandmemooffline.Dao.DoneTodoDao
import com.simplepro.todoandmemooffline.instance.DoneTodoInstance

@Database(entities = [DoneTodoInstance::class], version = 1)
abstract class DoneTodoDB : RoomDatabase() {
    abstract fun doneTodoDB() : DoneTodoDao
}