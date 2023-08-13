package com.example.budgetapplication.data

import androidx.room.TypeConverter
import java.time.LocalDateTime
import java.time.ZoneOffset

class DateConverter{
    @TypeConverter
    fun toDate(value: Long?): LocalDateTime? {
        return value?.let { LocalDateTime.ofEpochSecond(it, 0, ZoneOffset.UTC) }
    }

    @TypeConverter
    fun toTimestamp(value: LocalDateTime?): Long? {
        return value?.toEpochSecond(ZoneOffset.UTC)
    }
}