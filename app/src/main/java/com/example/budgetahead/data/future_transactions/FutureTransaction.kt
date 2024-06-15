package com.example.budgetahead.data.future_transactions

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.budgetahead.data.DateConverter
import com.example.budgetahead.data.categories.Category
import com.example.budgetahead.data.currencies.Currency
import com.example.budgetahead.data.transactions.TransactionType
import java.time.LocalDateTime

@Entity(
    tableName = "futureTransactions",
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("categoryId"),
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = Currency::class,
            parentColumns = arrayOf("name"),
            childColumns = arrayOf("currency"),
            onDelete = ForeignKey.RESTRICT
        )
    ]
)
data class FutureTransaction(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val type: TransactionType,
    val categoryId: Int,
    val amount: Float,
    val currency: String,
    @TypeConverters(DateConverter::class)
    val startDate: LocalDateTime,
    @TypeConverters(DateConverter::class)
    val endDate: LocalDateTime,
    val recurrenceType: RecurrenceType,
    val recurrenceValue: Int
)