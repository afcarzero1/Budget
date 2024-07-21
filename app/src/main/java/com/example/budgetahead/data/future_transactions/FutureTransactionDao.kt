package com.example.budgetahead.data.future_transactions

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FutureTransactionDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(futureTransaction: FutureTransaction)

    @Update
    suspend fun update(futureTransaction: FutureTransaction)

    @Delete
    suspend fun delete(futureTransaction: FutureTransaction)

    @Query("SELECT * from futureTransactions WHERE id = :id")
    fun getFutureTransactionStream(id: Int): Flow<FutureTransaction>

    @Query("SELECT * from futureTransactions ORDER BY amount DESC")
    fun getAllFutureTransactionsStream(): Flow<List<FutureTransaction>>

    @Transaction
    @Query("SELECT * from futureTransactions WHERE id = :id")
    fun getFutureFullTransactionStream(id: Int): Flow<FullFutureTransaction>

    @Transaction
    @Query("SELECT * from futureTransactions ORDER BY amount DESC")
    fun getAllFutureFullTransactionsStream(): Flow<List<FullFutureTransaction>>
}
