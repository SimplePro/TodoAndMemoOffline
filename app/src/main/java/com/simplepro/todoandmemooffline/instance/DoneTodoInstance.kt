package com.simplepro.todoandmemooffline.instance

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "doneTodo")
data class DoneTodoInstance (
    @ColumnInfo(name = "doneTodo") var doneTodo : String = "",
    @ColumnInfo(name = "doneTodoContent") var doneTodoContent : String = "",
    @PrimaryKey var doneTodoId : String = ""
)