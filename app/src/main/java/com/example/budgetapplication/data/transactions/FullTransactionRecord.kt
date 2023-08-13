package com.example.budgetapplication.data.transactions

import androidx.room.Embedded
import androidx.room.Relation
import com.example.budgetapplication.data.accounts.Account
import com.example.budgetapplication.data.categories.Category

data class FullTransactionRecord(
    @Embedded val transactionRecord: TransactionRecord,
    @Relation(
            parentColumn = "accountId",
            entityColumn = "id"
    )
    val account: Account,
    @Relation(
            parentColumn = "categoryId",
            entityColumn = "id"
    )
    val category: Category

)