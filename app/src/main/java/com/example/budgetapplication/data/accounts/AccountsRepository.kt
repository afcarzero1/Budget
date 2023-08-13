package com.example.budgetapplication.data.accounts

import kotlinx.coroutines.flow.Flow

interface AccountsRepository {

    suspend fun insertAccount(account: Account)

    suspend fun updateAccount(account: Account)

    suspend fun deleteAccount(account: Account)

    fun getAccountStream(id: Int): Flow<Account?>

    fun getAllAccountsStream(): Flow<List<Account>>

    fun getAccountWithTransactionsStream(id: Int): Flow<AccountWithTransactions>

    fun getAllAccountsWithTransactionsStream(): Flow<List<AccountWithTransactions>>
}