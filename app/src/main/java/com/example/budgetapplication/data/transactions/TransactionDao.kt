package com.example.budgetapplication.data.transactions

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.budgetapplication.data.transfers.Transfer
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(transactionRecord: TransactionRecord): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertMany(vararg transactionRecords: TransactionRecord): List<Long>

    @Transaction
    suspend fun insertTransferAndTransactions(transfer: Transfer) {
        // Insert source and destination transactions
        val sourceTransactionId = insert(
            TransactionRecord(
                id = 0,  // Auto-generate the ID
                name = transfer.destinationAccountId.toString(),
                type = TransactionType.EXPENSE_TRANSFER,
                accountId = transfer.sourceAccountId,
                categoryId = null,
                amount = transfer.amountSource,
                date = transfer.date
            )
        )

        val destinationTransactionId = insert(
            TransactionRecord(
                id = 0,  // Auto-generate the ID
                name = transfer.sourceAccountId.toString(),
                type = TransactionType.INCOME_TRANSFER,
                accountId = transfer.destinationAccountId,
                categoryId = null,
                amount = transfer.amountDestination,
                date = transfer.date
            )
        )

        // Assuming TransferDao is accessible or this method is in a Dao that also handles Transfer objects
        insertTransfer(
            transfer.copy(
                sourceAccountTransactionId = sourceTransactionId,
                destinationAccountTransactionId = destinationTransactionId
            )
        )
    }

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertTransfer(transfer: Transfer)

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
    @Query(
        "SELECT * from transactions " +
                "WHERE date >= :startDateTime AND date <= :endDateTime AND categoryId IS NOT NULL " +
                "ORDER BY date DESC"
    )
    fun getFullTransactionsByDateStream(
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime
    ): Flow<List<FullTransactionRecord>>

    @Transaction
    @Query("SELECT * from transactions WHERE categoryId IS NULL ORDER BY date DESC")
    fun getAllFullTransferTransactionsStream(): Flow<List<FullTransactionRecord>>
}