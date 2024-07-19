package com.example.budgetahead.data.transactions

import androidx.room.Embedded
import androidx.room.Relation
import com.example.budgetahead.data.accounts.Account
import com.example.budgetahead.data.accounts.AccountWithCurrency
import com.example.budgetahead.data.categories.Category

data class FullTransactionRecord(
    @Embedded val transactionRecord: TransactionRecord,
    @Relation(
        entity = Account::class,
        parentColumn = "accountId",
        entityColumn = "id"
    )
    val account: AccountWithCurrency,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "id"
    )
    val category: Category?
)
