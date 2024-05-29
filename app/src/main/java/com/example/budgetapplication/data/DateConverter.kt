package com.example.budgetapplication.data

import androidx.room.TypeConverter
import com.example.budgetapplication.data.categories.CategoryType
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


class CategoryTypeConverter {
    @TypeConverter
    fun fromDefaultType(defaultType: CategoryType): String {
        return defaultType.name
    }

    @TypeConverter
    fun toDefaultType(defaultType: String): CategoryType {
        return CategoryType.valueOf(defaultType)
    }
}


