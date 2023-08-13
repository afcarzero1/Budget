package com.example.budgetapplication.data.transactions

import kotlinx.coroutines.flow.Flow

class OfflineTransactionsRepository(
    private val transactionDao: TransactionDao
) : TransactionsRepository {
    override suspend fun insert(transactionRecord: TransactionRecord) =
        transactionDao.insert(transactionRecord)

    override suspend fun update(transactionRecord: TransactionRecord) =
        transactionDao.update(transactionRecord)

    override suspend fun delete(transactionRecord: TransactionRecord) =
        transactionDao.delete(transactionRecord)

    override fun getTransactionStream(id: Int): Flow<TransactionRecord> =
        transactionDao.getTransactionStream(id)

    override fun getAllTransactionsStream(): Flow<List<TransactionRecord>> =
        transactionDao.getAllTransactionsStream()

    override fun getFullTransactionStream(id: Int): Flow<FullTransactionRecord> =
        transactionDao.getFullTransactionStream(id)

    override fun getAllFullTransactionsStream(): Flow<List<FullTransactionRecord>> =
        transactionDao.getAllFullTransactionsStream()
}