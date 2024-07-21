package com.example.budgetahead.data.accounts

import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Relation
import com.example.budgetahead.data.currencies.Currency
import com.example.budgetahead.data.transactions.FullTransactionRecord
import com.example.budgetahead.data.transactions.TransactionRecord
import com.example.budgetahead.data.transfers.Transfer
import com.example.budgetahead.data.transfers.TransferWithAccounts

data class FullAccount(
    @Embedded val account: Account,
    @Relation(
        parentColumn = "currency",
        entityColumn = "name"
    )
    val currency: Currency,
    @Relation(
        entity = TransactionRecord::class,
        parentColumn = "id",
        entityColumn = "accountId"
    )
    val transactionRecords: List<FullTransactionRecord>,
    @Relation(
        entity = Transfer::class,
        parentColumn = "id",
        entityColumn = "destinationAccountId"
    )
    val transfersIncoming: List<TransferWithAccounts>,
    @Relation(
        entity = Transfer::class,
        parentColumn = "id",
        entityColumn = "sourceAccountId"
    )
    val transfersOutgoing: List<TransferWithAccounts>
) {
    @get:Ignore val balance: Float
        get() {
            return account.computeBalance(transactionRecords.map { it.transactionRecord })
        }
}
