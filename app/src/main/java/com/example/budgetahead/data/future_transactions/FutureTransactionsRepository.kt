package com.example.budgetahead.data.future_transactions

import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

interface FutureTransactionsRepository {
    suspend fun insert(futureTransaction: FutureTransaction)

    suspend fun update(futureTransaction: FutureTransaction)

    suspend fun delete(futureTransaction: FutureTransaction)

    fun getFutureTransactionStream(id: Int): Flow<FutureTransaction>

    fun getAllFutureTransactionsStream(): Flow<List<FutureTransaction>>

    @Transaction
    fun getFutureFullTransactionStream(id: Int): Flow<FullFutureTransaction>

    @Transaction
    fun getAllFutureFullTransactionsStream(): Flow<List<FullFutureTransaction>>
}
