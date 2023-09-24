package com.example.budgetapplication.data.transactions

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.budgetapplication.data.DateConverter
import com.example.budgetapplication.data.accounts.Account
import com.example.budgetapplication.data.categories.Category
import java.time.LocalDateTime

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = Account::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("accountId"),
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = Category::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("categoryId"),
            onDelete = ForeignKey.RESTRICT
        )
    ]
)
data class TransactionRecord (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val type: TransactionType,
    val accountId: Int,
    val categoryId: Int,
    val amount: Float,
    @TypeConverters(DateConverter::class)
    val date: LocalDateTime,
)