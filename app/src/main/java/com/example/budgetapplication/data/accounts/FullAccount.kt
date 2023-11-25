package com.example.budgetapplication.data.accounts

import androidx.room.Embedded
import androidx.room.Relation
import com.example.budgetapplication.data.currencies.Currency
import com.example.budgetapplication.data.transactions.TransactionRecord
import androidx.room.Ignore
import com.example.budgetapplication.data.transactions.FullTransactionRecord

data class FullAccount(
    @Embedded val account: Account,
    @Relation(
        parentColumn = "currency",
        entityColumn = "name"
    )
    val currency: Currency,
    @Relation(
        entity = TransactionRecord::class,
        parentColumn = "id",
        entityColumn = "accountId"
    )
    val transactionRecords: List<FullTransactionRecord>
){
    @get:Ignore val balance: Float
        get() {
            return account.computeBalance(transactionRecords.map { it.transactionRecord })
        }
}