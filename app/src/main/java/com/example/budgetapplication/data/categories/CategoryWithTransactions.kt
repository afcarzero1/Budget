package com.example.budgetapplication.data.categories

import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Relation
import com.example.budgetapplication.data.transactions.FullTransactionRecord
import com.example.budgetapplication.data.transactions.TransactionRecord
import com.example.budgetapplication.data.transactions.TransactionType
import com.example.budgetapplication.data.transactions.TransactionWithCurrency
import org.jetbrains.annotations.Nullable


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