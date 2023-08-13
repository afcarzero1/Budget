package com.example.budgetapplication.data.currencies

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.budgetapplication.data.DateConverter
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
