package com.example.budgetapplication.data.accounts

import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Relation
import com.example.budgetapplication.data.transactions.TransactionRecord


data class AccountWithTransactions(
    @Embedded val account: Account,
    @Relation(
        parentColumn = "id",
        entityColumn = "accountId"
    )
    val transactionRecords: List<TransactionRecord>,

){
    @get:Ignore val balance: Float
        get() {
            var balance = account.initialBalance

            for (transactionRecord in transactionRecords) {
                if (transactionRecord.type == "Expense") {
                    balance -= transactionRecord.amount
                } else {
                    balance += transactionRecord.amount
                }
            }

            return balance
        }
}
