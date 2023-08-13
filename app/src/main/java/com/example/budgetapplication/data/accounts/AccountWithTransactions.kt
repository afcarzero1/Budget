package com.example.budgetapplication.data.accounts

import androidx.room.Embedded
import androidx.room.Relation
import com.example.budgetapplication.data.transactions.TransactionRecord

data class AccountWithTransactions(
    @Embedded val account: Account,
    @Relation(
        parentColumn = "id",
        entityColumn = "accountId"
    )
    val transactionRecords: List<TransactionRecord>
)