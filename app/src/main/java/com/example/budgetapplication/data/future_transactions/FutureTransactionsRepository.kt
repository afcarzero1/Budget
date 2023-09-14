package com.example.budgetapplication.data.future_transactions

import com.example.budgetapplication.data.transactions.FullTransactionRecord
import com.example.budgetapplication.data.transactions.TransactionRecord
import kotlinx.coroutines.flow.Flow

interface FutureTransactionsRepository {

    suspend fun insert(futureTransaction: FutureTransaction)

    suspend fun update(futureTransaction: FutureTransaction)

    suspend fun delete(futureTransaction: FutureTransaction)

    fun getFutureTransactionStream(id: Int): Flow<FutureTransaction>

    fun getAllFutureTransactionsStream(): Flow<List<FutureTransaction>>

}