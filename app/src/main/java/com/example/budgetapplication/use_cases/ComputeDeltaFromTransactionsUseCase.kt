package com.example.budgetapplication.use_cases

import com.example.budgetapplication.data.currencies.Currency
import com.example.budgetapplication.data.transactions.TransactionRecord

class ComputeDeltaFromTransactionsUseCase {

    /**
     * Computes the delta in the base currency
     */
    fun computeDelta(transactions: List<Pair<TransactionRecord, Currency>>): Float {
        var delta = 0f  // Initialize delta as a floating point number
        for (transaction in transactions) {
            val amountInBaseCurrency = transaction.first.amount * transaction.second.value
            delta += amountInBaseCurrency
        }
        return delta
    }
}