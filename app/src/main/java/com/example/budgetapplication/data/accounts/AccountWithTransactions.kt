package com.example.budgetapplication.data.accounts

import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Relation
import com.example.budgetapplication.data.transactions.FullTransactionRecord
import com.example.budgetapplication.data.transactions.TransactionRecord


data class AccountWithTransactions(
    @Embedded val account: Account,
    @Relation(
        entity = TransactionRecord::class,
        parentColumn = "id",
        entityColumn = "accountId"
    )
    val transactionRecords: List<TransactionRecord>,

    ){
    @get:Ignore val balance: Float
        get() {
            return account.computeBalance(transactionRecords)
        }
}
