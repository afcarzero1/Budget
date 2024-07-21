package com.example.budgetahead.data.accounts

import com.example.budgetahead.data.currencies.Currency
import com.example.budgetahead.data.transfers.Transfer
import kotlinx.coroutines.flow.Flow

interface AccountsRepository {
    suspend fun insertAccount(account: Account)

    suspend fun updateAccount(account: Account)

    suspend fun deleteAccount(account: Account)

    fun getAccountStream(id: Int): Flow<Account?>

    fun getAllAccountsStream(): Flow<List<Account>>

    fun getAccountWithTransactionsStream(id: Int): Flow<AccountWithTransactions>

    fun getAllAccountsWithTransactionsStream(): Flow<List<AccountWithTransactions>>

    fun getFullAccountStream(id: Int): Flow<FullAccount>

    fun getAllFullAccountsStream(): Flow<List<FullAccount>>

    fun totalBalance(): Flow<Pair<Currency, Float>>

    suspend fun registerTransfer(transfer: Transfer)
}
