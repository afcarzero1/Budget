package com.example.budgetapplication.data.future_transactions

import kotlinx.coroutines.flow.Flow

class OfflineFutureTransactionsRepository(
    private val futureTransactionDao: FutureTransactionDao
) : FutureTransactionsRepository{
    override suspend fun insert(futureTransaction: FutureTransaction) = futureTransactionDao.insert(futureTransaction)

    override suspend fun update(futureTransaction: FutureTransaction) = futureTransactionDao.update(futureTransaction)

    override suspend fun delete(futureTransaction: FutureTransaction) = futureTransactionDao.delete(futureTransaction)

    override fun getFutureTransactionStream(id: Int): Flow<FutureTransaction> = futureTransactionDao.getFutureTransactionStream(id)

    override fun getAllFutureTransactionsStream(): Flow<List<FutureTransaction>> = futureTransactionDao.getAllFutureTransactionsStream()
}