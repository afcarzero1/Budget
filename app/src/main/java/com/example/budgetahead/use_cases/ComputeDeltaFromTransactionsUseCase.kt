package com.example.budgetahead.use_cases

import com.example.budgetahead.data.currencies.Currency
import com.example.budgetahead.data.transactions.FullTransactionRecord
import com.example.budgetahead.data.transactions.TransactionRecord

class ComputeDeltaFromTransactionsUseCase {
    /**
     * Computes the delta in the base currency
     */
    @JvmName("computeDeltaPairOfTransactionAndCurrency")
    fun computeDelta(transactions: List<Pair<TransactionRecord, Currency>>): Float {
        var delta = 0f // Initialize delta as a floating point number
        for (transaction in transactions) {
            val amountInBaseCurrency = toBaseCurrency(transaction.first.amount, transaction.second)
            delta += amountInBaseCurrency
        }
        return delta
    }

    @JvmName("computeDeltaFullTransactionRecord")
    fun computeDelta(transactions: List<FullTransactionRecord>): Float {
        var delta = 0f // Initialize delta as a floating point number
        for (transaction in transactions) {
            val amountInBaseCurrency = toBaseCurrency(transaction.transactionRecord.amount, transaction.account.currency)
            delta += amountInBaseCurrency
        }
        return delta
    }

    // TODO: Add here protection for currencies that are too close to 0 in value.
    fun toBaseCurrency(
        amount: Float,
        currency: Currency,
    ): Float = amount * 1 / currency.value

    fun fromBaseCurrency(
        amount: Float,
        currency: Currency,
    ): Float = amount * currency.value
}
