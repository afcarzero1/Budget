package com.example.budgetapplication.data.accounts

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.budgetapplication.data.currencies.Currency
import com.example.budgetapplication.data.transactions.TransactionRecord
import com.example.budgetapplication.data.transactions.TransactionType
import java.time.LocalDate


@Entity(
    tableName = "accounts",
    foreignKeys = [ForeignKey(
        entity = Currency::class,
        parentColumns = arrayOf("name"),
        childColumns = arrayOf("currency"),
        onDelete = ForeignKey.RESTRICT
    )]
)
data class Account(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val initialBalance: Float,
    val currency: String,
    val color: Long = 0xFF0000FF,
    val hidden: Boolean = false
) {

    /**
     * Compute the balance of the account at the start of the day (not considering transactions
     * that happened the "atDate").
     * @param transactionRecords the list of transactions to consider
     * @param atDate the date to consider
     */
    fun computeBalance(
        transactionRecords: List<TransactionRecord>,
        atDate: LocalDate = LocalDate.now()
    ): Float {
        var balance = initialBalance

        for (transactionRecord in transactionRecords) {
            if (transactionRecord.date.toLocalDate() > atDate) {
                continue
            }

            when (transactionRecord.type) {
                TransactionType.EXPENSE -> balance -= transactionRecord.amount
                TransactionType.INCOME -> balance += transactionRecord.amount
                TransactionType.INCOME_TRANSFER -> balance += transactionRecord.amount
                TransactionType.EXPENSE_TRANSFER -> balance -= transactionRecord.amount
            }
        }

        return balance
    }

}
