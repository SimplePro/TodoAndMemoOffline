package com.simplepro.secondtodoandmemo.instance

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "memo")
data class MemoInstance(//
    @ColumnInfo(name = "memoTitle") val memoTitle: String = "",
    @ColumnInfo(name = "memoContent") val memoContent: String = "",
    @ColumnInfo(name = "memoCalendar") val memoCalendar: String = "",
    @ColumnInfo(name = "memoPlan") val memoPlan: String = "",
    @PrimaryKey val memoId : String = "")