package com.example.budgetapplication.data.accounts

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.budgetapplication.data.currencies.Currency


@Entity(tableName = "accounts",
    foreignKeys = [ForeignKey(
        entity = Currency::class,
        parentColumns = arrayOf("name"),
        childColumns = arrayOf("currency"),
        onDelete = ForeignKey.RESTRICT
    )]
)
data class Account (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val initialBalance: Float,
    val currency: String
)
