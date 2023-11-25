package com.example.budgetapplication.data.accounts

import androidx.room.Embedded
import androidx.room.Relation
import com.example.budgetapplication.data.currencies.Currency
import com.example.budgetapplication.data.transactions.TransactionRecord

data class AccountWithCurrency(
    @Embedded val account: Account,
    @Relation(
        parentColumn = "currency",
        entityColumn = "name"
    )
    val currency: Currency,
)