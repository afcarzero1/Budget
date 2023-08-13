package com.example.budgetapplication.data.accounts

import kotlinx.coroutines.flow.Flow

class OfflineAccountsRepository(private val accountDao: AccountDao) : AccountsRepository {
    override suspend fun insertAccount(account: Account) = accountDao.insert(account)

    override suspend fun updateAccount(account: Account) = accountDao.update(account)

    override suspend fun deleteAccount(account: Account) = accountDao.delete(account)

    override fun getAccountStream(id: Int): Flow<Account?> = accountDao.getAccount(id)

    override fun getAllAccountsStream(): Flow<List<Account>> = accountDao.getAllAccounts()
}