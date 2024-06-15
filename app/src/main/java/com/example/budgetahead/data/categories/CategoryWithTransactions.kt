package com.example.budgetahead.data.categories

import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Relation
import com.example.budgetahead.data.transactions.TransactionRecord
import com.example.budgetahead.data.transactions.TransactionWithCurrency


data class CategoryWithTransactions(
    @Embedded val category: Category,
    @Relation(
            entity = TransactionRecord::class,
            parentColumn = "id",
            entityColumn = "categoryId"
    )
    val transactions: List<TransactionWithCurrency>
){
    @get:Ignore val historicalBalance: Float
        get() {
            TODO("Implement properly with currencies")
        }
}