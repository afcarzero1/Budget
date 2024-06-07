package com.example.budgetapplication.data.transfers

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.example.budgetapplication.data.accounts.Account
import com.example.budgetapplication.data.transactions.TransactionRecord
import java.time.LocalDateTime


@Entity(
    tableName = "transfers",
    foreignKeys = [
        ForeignKey(
            entity = Account::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("sourceAccountId"),
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Account::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("destinationAccountId"),
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TransactionRecord::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("sourceAccountTransactionId"),
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TransactionRecord::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("destinationAccountTransactionId"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Transfer(
    @PrimaryKey val id: Int,
    val sourceAccountId: Int,
    val sourceAccountTransactionId: Long,
    val destinationAccountId: Int,
    val destinationAccountTransactionId: Long,
    val amountSource: Float,
    val amountDestination: Float,
    val date: LocalDateTime
)


data class TransferWithAccounts(
    @Embedded val transfer: Transfer,
    @Relation(
        parentColumn = "sourceAccountId",
        entityColumn = "id"
    )
    val sourceAccount: Account,
    @Relation(
        parentColumn = "destinationAccountId",
        entityColumn = "id"
    )
    val destinationAccount: Account
)