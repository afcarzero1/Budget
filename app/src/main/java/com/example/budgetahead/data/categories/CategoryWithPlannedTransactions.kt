package com.example.budgetahead.data.categories

import androidx.room.Embedded
import androidx.room.Relation
import com.example.budgetahead.data.future_transactions.FullFutureTransaction
import com.example.budgetahead.data.future_transactions.FutureTransaction

data class CategoryWithPlannedTransactions(
    @Embedded val category: Category,
    @Relation(
        entity = FutureTransaction::class,
        parentColumn = "id",
        entityColumn = "categoryId"
    )
    val transactions: List<FullFutureTransaction>
)
