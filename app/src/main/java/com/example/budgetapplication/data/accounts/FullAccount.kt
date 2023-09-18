package com.example.budgetapplication.data.accounts

import androidx.room.Embedded
import androidx.room.Relation
import com.example.budgetapplication.data.currencies.Currency
import com.example.budgetapplication.data.transactions.TransactionRecord
import androidx.room.Ignore

data class FullAccount(
    @Embedded val account: Account,
    @Relation(
        parentColumn = "currency", // Assuming you have a currencyId field in your Account table
        entityColumn = "name"
    )
    val currency: Currency,
    @Relation(
        parentColumn = "id",
        entityColumn = "accountId"
    )
    val transactionRecords: List<TransactionRecord>
){
    @get:Ignore val balance: Float
        get() {
            return account.computeBalance(transactionRecords)
        }
}