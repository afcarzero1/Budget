package com.example.budgetahead.data.accounts

import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Relation
import com.example.budgetahead.data.currencies.Currency
import com.example.budgetahead.data.transactions.FullTransactionRecord
import com.example.budgetahead.data.transactions.TransactionRecord

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
) {
    @get:Ignore val balance: Float
        get() {
            return account.computeBalance(transactionRecords.map { it.transactionRecord })
        }
}
