package com.example.budgetapplication.data.transactions

import com.example.budgetapplication.data.transfers.Transfer
import kotlinx.coroutines.flow.Flow
import java.time.YearMonth

interface TransactionsRepository {
    suspend fun insert(transactionRecord: TransactionRecord)

    suspend fun insertMany(vararg transactionRecord: TransactionRecord)

    suspend fun insertTransfer(transfer: Transfer)

    suspend fun update(transactionRecord: TransactionRecord)

    suspend fun delete(transactionRecord: TransactionRecord)

    fun getTransactionStream(id: Int): Flow<TransactionRecord>

    fun getAllTransactionsStream(): Flow<List<TransactionRecord>>

    fun getFullTransactionStream(id: Int): Flow<FullTransactionRecord>

    fun getAllFullTransactionsStream(): Flow<List<FullTransactionRecord>>

    fun getFullTransactionsByMonthsStream(
        fromDate: YearMonth,
        toDate: YearMonth,
    ): Flow<List<FullTransactionRecord>>

    fun getAllFullTransferTransactionsStream(): Flow<List<FullTransactionRecord>>
}