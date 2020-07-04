package com.simplepro.secondtodoandmemo.instance

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todo")
data class TodoInstance (
    @ColumnInfo(name = "todo") val todo : String = "",
    @ColumnInfo(name = "content") val content : String = "",
    @PrimaryKey val todoId: String = "")