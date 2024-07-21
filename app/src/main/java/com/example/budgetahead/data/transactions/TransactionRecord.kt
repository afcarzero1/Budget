package com.example.budgetahead.data.transactions

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.budgetahead.data.DateConverter
import com.example.budgetahead.data.accounts.Account
import com.example.budgetahead.data.categories.Category
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
data class TransactionRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val type: TransactionType,
    val accountId: Int,
    val categoryId: Int?,
    val amount: Float,
    @TypeConverters(DateConverter::class)
    val date: LocalDateTime
) {
    // I prefer to throw errors than to have inconsistent data
    init {
        if (type in listOf(TransactionType.EXPENSE_TRANSFER, TransactionType.INCOME_TRANSFER) &&
            categoryId != null
        ) {
            throw IllegalArgumentException(
                "categoryId must be null for EXPENSE_TRANSFER or INCOME_TRANSFER transactions"
            )
        } else if (type !in
            listOf(TransactionType.EXPENSE_TRANSFER, TransactionType.INCOME_TRANSFER) &&
            categoryId == null
        ) {
            throw IllegalArgumentException(
                "categoryId cannot be null for EXPENSE or INCOME transactions"
            )
        }
    }
}
