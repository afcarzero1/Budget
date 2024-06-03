package com.example.budgetapplication.data.transactions

import androidx.room.Embedded
import androidx.room.Relation
import com.example.budgetapplication.data.accounts.Account
import com.example.budgetapplication.data.accounts.AccountWithCurrency

class TransactionWithCurrency(
    @Embedded val transactionRecord: TransactionRecord,
    @Relation(
        entity = Account::class,
        parentColumn = "accountId",
        entityColumn = "id"
    ) val account: AccountWithCurrency
)