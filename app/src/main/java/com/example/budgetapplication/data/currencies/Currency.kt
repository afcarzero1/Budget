package com.example.budgetapplication.data.currencies

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.time.LocalDateTime
import java.time.ZoneOffset


@Entity(tableName = "currencies")
data class Currency(
    @PrimaryKey
    val name: String,
    val value: Float,
    @TypeConverters(DateConverter::class)
    val updatedTime: LocalDateTime
)

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