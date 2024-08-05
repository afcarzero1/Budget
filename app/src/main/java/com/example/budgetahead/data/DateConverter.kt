package com.example.budgetahead.data

import androidx.room.TypeConverter
import com.example.budgetahead.data.categories.CategoryType
import java.time.LocalDateTime
import java.time.ZoneOffset

class DateConverter {
    @TypeConverter
    fun toDate(value: Long?): LocalDateTime? =
        value?.let { LocalDateTime.ofEpochSecond(it, 0, ZoneOffset.UTC) }

    @TypeConverter
    fun toTimestamp(value: LocalDateTime?): Long? = value?.toEpochSecond(ZoneOffset.UTC)
}

class CategoryTypeConverter {
    @TypeConverter
    fun fromDefaultType(defaultType: CategoryType): String = defaultType.name

    @TypeConverter
    fun toDefaultType(defaultType: String): CategoryType = CategoryType.valueOf(defaultType)
}
