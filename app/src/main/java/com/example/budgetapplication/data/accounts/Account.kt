package com.example.budgetapplication.data.accounts

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.budgetapplication.data.currencies.Currency
import com.example.budgetapplication.data.transactions.TransactionRecord
import com.example.budgetapplication.data.transactions.TransactionType


@Entity(tableName = "accounts",
    foreignKeys = [ForeignKey(
        entity = Currency::class,
        parentColumns = arrayOf("name"),
        childColumns = arrayOf("currency"),
        onDelete = ForeignKey.RESTRICT
    )]
)
data class Account (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val initialBalance: Float,
    val currency: String,
    val color: Long = 0x000000
){

    fun computeBalance(transactionRecords: List<TransactionRecord>): Float {
        var balance = initialBalance

        for (transactionRecord in transactionRecords) {
            if (transactionRecord.type == TransactionType.EXPENSE) {
                balance -= transactionRecord.amount
            } else if (transactionRecord.type == TransactionType.INCOME) {
                balance += transactionRecord.amount
            }
        }

        return balance
    }

}
