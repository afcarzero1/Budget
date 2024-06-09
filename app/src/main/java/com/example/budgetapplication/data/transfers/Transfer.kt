package com.example.budgetapplication.data.transfers

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.example.budgetapplication.data.accounts.Account
import com.example.budgetapplication.data.accounts.AccountWithCurrency
import com.example.budgetapplication.data.transactions.TransactionRecord
import java.time.LocalDateTime


@Entity(
    tableName = "transfers",
    foreignKeys = [
        ForeignKey(
            entity = Account::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("sourceAccountId"),
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = Account::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("destinationAccountId"),
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = TransactionRecord::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("sourceAccountTransactionId"),
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = TransactionRecord::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("destinationAccountTransactionId"),
            onDelete = ForeignKey.RESTRICT
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
        entity = Account::class,
        parentColumn = "sourceAccountId",
        entityColumn = "id"
    )
    val sourceAccount: AccountWithCurrency,
    @Relation(
        entity = Account::class,
        parentColumn = "destinationAccountId",
        entityColumn = "id"
    )
    val destinationAccount: AccountWithCurrency
)