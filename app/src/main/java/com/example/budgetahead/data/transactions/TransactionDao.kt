package com.example.budgetahead.data.transactions

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.budgetahead.data.transfers.Transfer
import com.example.budgetahead.data.transfers.TransferWithAccounts
import java.time.LocalDateTime
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(transactionRecord: TransactionRecord): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertMany(vararg transactionRecords: TransactionRecord): List<Long>

    @Transaction
    suspend fun insertTransferAndTransactions(transfer: Transfer) {
        val (sourceRecord, destinationRecord) = transactionsFromTransfer(transfer)

        val sourceRecordId = insert(sourceRecord)
        val destinationRecordId = insert(destinationRecord)

        insertTransfer(
            transfer.copy(
                sourceAccountTransactionId = sourceRecordId,
                destinationAccountTransactionId = destinationRecordId
            )
        )
    }

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertTransfer(transfer: Transfer)

    @Update
    suspend fun update(transactionRecord: TransactionRecord)

    @Update
    suspend fun updateTransfer(transfer: Transfer)

    @Transaction
    suspend fun updateTransferAndTransactions(transfer: Transfer) {
        val (sourceRecord, destinationRecord) =
            transactionsFromTransfer(
                transfer,
                transfer.sourceAccountTransactionId.toInt(),
                transfer.destinationAccountTransactionId.toInt()
            )

        updateTransfer(transfer)

        // We update the associated records
        update(sourceRecord)
        update(destinationRecord)
    }

    @Delete
    suspend fun deleteTransaction(transactionRecord: TransactionRecord)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteTransaction(id: Int)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteTransaction(id: Long)

    @Delete
    suspend fun deleteTransfer(transfer: Transfer)

    @Transaction
    suspend fun deleteTransferAndTransactions(transfer: Transfer) {
        // Transfer deletion must happen before
        deleteTransfer(transfer)

        // Then we delete the transactions (parents)
        deleteTransaction(transfer.sourceAccountTransactionId)
        deleteTransaction(transfer.destinationAccountTransactionId)
    }

    @Query("SELECT * from transactions WHERE id = :id")
    fun getTransactionStream(id: Int): Flow<TransactionRecord>

    @Query("SELECT * from transactions ORDER BY date DESC")
    fun getAllTransactionsStream(): Flow<List<TransactionRecord>>

    @Query("SELECT * FROM transfers WHERE :id = id")
    fun getTransferStream(id: Int): Flow<Transfer>

    @Query("SELECT * FROM transfers ORDER BY date DESC")
    fun getAllTransfersStream(): Flow<List<Transfer>>

    @Transaction
    @Query("SELECT * FROM transfers ORDER BY date DESC")
    fun getAllTransfersWithAccountsStream(): Flow<List<TransferWithAccounts>>

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

    private fun transactionsFromTransfer(
        transfer: Transfer,
        sourceId: Int = 0,
        destinationId: Int = 0
    ): Pair<TransactionRecord, TransactionRecord> {
        val sourceTransaction =
            TransactionRecord(
                id = sourceId, // Auto-generate the ID
                name = transfer.destinationAccountId.toString(),
                type = TransactionType.EXPENSE_TRANSFER,
                accountId = transfer.sourceAccountId,
                categoryId = null,
                amount = transfer.amountSource,
                date = transfer.date
            )

        val destinationTransaction =
            TransactionRecord(
                id = destinationId, // Auto-generate the ID
                name = transfer.sourceAccountId.toString(),
                type = TransactionType.INCOME_TRANSFER,
                accountId = transfer.destinationAccountId,
                categoryId = null,
                amount = transfer.amountDestination,
                date = transfer.date
            )
        return Pair(sourceTransaction, destinationTransaction)
    }
}
