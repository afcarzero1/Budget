package com.example.budgetapplication.data.currencies

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Entity(tableName = "currencies")
data class Currency(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val value: Float,
    val updatedTime: String
)