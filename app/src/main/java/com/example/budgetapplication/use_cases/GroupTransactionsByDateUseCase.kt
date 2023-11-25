package com.example.budgetapplication.use_cases

import com.example.budgetapplication.data.transactions.TransactionRecord
import com.example.budgetapplication.data.transactions.TransactionType
import java.time.LocalDate

class GroupTransactionsByDateUseCase {

    fun execute(transactions: List<TransactionRecord>, includeTransfers: Boolean): Map<LocalDate, List<TransactionRecord>> {
        return try {
            transactions.filter {
                includeTransfers || it.type !in listOf(TransactionType.EXPENSE_TRANSFER, TransactionType.INCOME_TRANSFER)
            }.groupBy { it.date.toLocalDate() }
        } catch (e: IllegalArgumentException) {
            throw e
        }
    }
}