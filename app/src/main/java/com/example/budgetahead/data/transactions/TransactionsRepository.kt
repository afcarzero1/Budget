package com.example.budgetahead.data.transactions

import com.example.budgetahead.data.transfers.Transfer
import com.example.budgetahead.data.transfers.TransferWithAccounts
import kotlinx.coroutines.flow.Flow
import java.time.YearMonth

interface TransactionsRepository {
    suspend fun insert(transactionRecord: TransactionRecord)

    suspend fun insertMany(vararg transactionRecord: TransactionRecord)

    suspend fun insertTransfer(transfer: Transfer)

    suspend fun update(transactionRecord: TransactionRecord)

    suspend fun updateTransfer(transfer: Transfer)

    suspend fun delete(transactionRecord: TransactionRecord)

    suspend fun deleteTransfer(transfer: Transfer)

    fun getTransactionStream(id: Int): Flow<TransactionRecord>

    fun getAllTransactionsStream(): Flow<List<TransactionRecord>>

    fun getTransfersStream(id: Int): Flow<Transfer>

    fun getAllTransfersStream(): Flow<List<Transfer>>

    fun getAllTransfersWithAccountsStream(): Flow<List<TransferWithAccounts>>

    fun getFullTransactionStream(id: Int): Flow<FullTransactionRecord>

    fun getAllFullTransactionsStream(): Flow<List<FullTransactionRecord>>

    fun getFullTransactionsByMonthsStream(
        fromDate: YearMonth,
        toDate: YearMonth,
    ): Flow<List<FullTransactionRecord>>

    fun getAllFullTransferTransactionsStream(): Flow<List<FullTransactionRecord>>
}