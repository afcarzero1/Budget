package com.example.budgetapplication.data.categories

import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Relation
import com.example.budgetapplication.data.transactions.TransactionRecord


data class CategoryWithTransactions(
    @Embedded val category: Category,
    @Relation(
            parentColumn = "id",
            entityColumn = "categoryId"
    )
    val transactions: List<TransactionRecord>
){
    @get:Ignore val historicalBalance: Float
        get() {
            var balance = 0f

            for (transactionRecord in transactions) {
                if (transactionRecord.type == "Expense") {
                    balance -= transactionRecord.amount
                } else {
                    balance += transactionRecord.amount
                }
            }

            return balance
        }



}