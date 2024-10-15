package com.example.budgetahead.data.transactions

import com.example.budgetahead.data.transfers.Transfer
import com.example.budgetahead.data.transfers.TransferWithAccounts
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import java.time.YearMonth

class OfflineTransactionsRepository(
    private val transactionDao: TransactionDao,
) : TransactionsRepository {
    override suspend fun insert(transactionRecord: TransactionRecord) {
        transactionDao.insert(transactionRecord)
    }

    override suspend fun insertMany(vararg transactionRecord: TransactionRecord) {
        transactionDao.insertMany(*transactionRecord)
    }

    override suspend fun insertTransfer(transfer: Transfer) {
        transactionDao.insertTransferAndTransactions(transfer)
    }

    override suspend fun update(transactionRecord: TransactionRecord) = transactionDao.update(transactionRecord)

    override suspend fun updateTransfer(transfer: Transfer) {
        transactionDao.updateTransferAndTransactions(transfer)
    }

    override suspend fun delete(transactionRecord: TransactionRecord) = transactionDao.deleteTransaction(transactionRecord)

    override suspend fun deleteTransfer(transfer: Transfer) {
        transactionDao.deleteTransferAndTransactions(transfer)
    }

    override fun getTransactionStream(id: Int): Flow<TransactionRecord> = transactionDao.getTransactionStream(id)

    override fun getTransfersStream(id: Int): Flow<Transfer> = transactionDao.getTransferStream(id)

    override fun getAllTransactionsStream(): Flow<List<TransactionRecord>> = transactionDao.getAllTransactionsStream()

    override fun getAllTransfersStream(): Flow<List<Transfer>> = transactionDao.getAllTransfersStream()

    override fun getAllTransfersWithAccountsStream(): Flow<List<TransferWithAccounts>> = transactionDao.getAllTransfersWithAccountsStream()

    override fun getFullTransactionStream(id: Int): Flow<FullTransactionRecord> = transactionDao.getFullTransactionStream(id)

    override fun getAllFullTransactionsStream(): Flow<List<FullTransactionRecord>> = transactionDao.getAllFullTransactionsStream()

    override fun getFullTransactionsByMonthsStream(
        fromDate: YearMonth,
        toDate: YearMonth,
    ): Flow<List<FullTransactionRecord>> {
        // Calculate the start and end date times for the specified start and end months/years
        val startDateTime = LocalDateTime.of(fromDate.year, fromDate.monthValue, 1, 0, 0)
        val endDateTime =
            LocalDateTime.of(toDate.year, toDate.monthValue, toDate.month.maxLength(), 23, 59)

        // Call the Dao function to get the transactions within the specified date range
        return transactionDao.getFullTransactionsByDateStream(startDateTime, endDateTime)
    }

    override fun getAllFullTransferTransactionsStream(): Flow<List<FullTransactionRecord>> {
        TODO("Not tested yet")
        return transactionDao.getAllFullTransferTransactionsStream()
    }
}
