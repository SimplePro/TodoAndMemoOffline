package com.simplepro.todoandmemooffline.instance

import android.app.AlarmManager
import android.app.PendingIntent
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter

@Entity(tableName = "todo")
data class TodoInstance (
    @ColumnInfo(name = "todo") val todo : String = "",
    @ColumnInfo(name = "content") val content : String = "",
    @ColumnInfo(name = "hour") val hour : Int = 0,
    @ColumnInfo(name = "minute") val minute : Int = 0,
////    @ColumnInfo(name = "pendingIntent")
//    val pendingIntent : PendingIntent,
////    @ColumnInfo(name = "alarmManager")
//    val alarmManger : AlarmManager,
    @ColumnInfo(name = "requestCode") val requestCode : Int = 0,
    @PrimaryKey val todoId: String = "")