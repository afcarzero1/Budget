package com.example.budgetapplication.data.categories

import androidx.room.Embedded
import androidx.room.Relation
import com.example.budgetapplication.data.transactions.TransactionRecord


data class CategoryWithTransactions(
    @Embedded val category: Category,
    @Relation(
            parentColumn = "id",
            entityColumn = "categoryId"
    )
    val transactions: List<TransactionRecord>
)