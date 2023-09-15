package com.example.budgetapplication.data.future_transactions

import androidx.room.Embedded
import androidx.room.Relation
import com.example.budgetapplication.data.accounts.AccountWithCurrency
import com.example.budgetapplication.data.categories.Category
import com.example.budgetapplication.data.currencies.Currency

data class FullFutureTransaction(
    @Embedded val futureTransaction: FutureTransaction,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "id"
    )
    val category: Category,
    @Relation(
        parentColumn = "currency",
        entityColumn = "name"
    )
    val currency: Currency
)