package com.example.budgetapplication.data.transactions

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(transactionRecord: TransactionRecord)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertMany(vararg transactionRecords: TransactionRecord)

    @Update
    suspend fun update(transactionRecord: TransactionRecord)

    @Delete
    suspend fun delete(transactionRecord: TransactionRecord)

    @Query("SELECT * from transactions WHERE id = :id")
    fun getTransactionStream(id: Int): Flow<TransactionRecord>

    @Query("SELECT * from transactions ORDER BY date DESC")
    fun getAllTransactionsStream(): Flow<List<TransactionRecord>>

    @Transaction
    @Query("SELECT * from transactions WHERE id = :id AND categoryId IS NOT NULL")
    fun getFullTransactionStream(id: Int): Flow<FullTransactionRecord>

    @Transaction
    @Query("SELECT * from transactions WHERE categoryId IS NOT NULL ORDER BY date DESC")
    fun getAllFullTransactionsStream(): Flow<List<FullTransactionRecord>>

    @Transaction
    @Query("SELECT * from transactions " +
            "WHERE date >= :startDateTime AND date <= :endDateTime AND categoryId IS NOT NULL " +
            "ORDER BY date DESC")
    fun getFullTransactionsByDateStream(startDateTime: LocalDateTime, endDateTime: LocalDateTime): Flow<List<FullTransactionRecord>>
}