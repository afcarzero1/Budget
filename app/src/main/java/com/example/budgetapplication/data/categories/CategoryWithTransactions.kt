package com.example.budgetapplication.data.categories

import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Relation
import com.example.budgetapplication.data.transactions.FullTransactionRecord
import com.example.budgetapplication.data.transactions.TransactionRecord
import com.example.budgetapplication.data.transactions.TransactionType


data class CategoryWithTransactions(
    @Embedded val category: Category,
    @Relation(
            entity = TransactionRecord::class,
            parentColumn = "id",
            entityColumn = "categoryId"
    )
    val transactions: List<TransactionRecord>
){
    @get:Ignore val historicalBalance: Float
        get() {
            TODO("Implement properly with currencies")
        }
}