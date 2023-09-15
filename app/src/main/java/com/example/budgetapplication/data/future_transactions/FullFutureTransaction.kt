package com.example.budgetapplication.data.future_transactions

import androidx.room.Embedded
import androidx.room.Relation
import com.example.budgetapplication.data.accounts.AccountWithCurrency
import com.example.budgetapplication.data.categories.Category

data class FullFutureTransaction(
    @Embedded val futureTransaction: FutureTransaction,
    @Relation(
        parentColumn = "accountId",
        entityColumn = "id"
    )
    val account: AccountWithCurrency,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "id"
    )
    val category: Category

)