package com.example.budgetapplication.data.accounts

import com.example.budgetapplication.data.currencies.Currency
import com.example.budgetapplication.data.transfers.Transfer
import kotlinx.coroutines.flow.Flow

class MockAccountRepository(
    private val accounts: List<Account>
): AccountsRepository {
    override suspend fun insertAccount(account: Account) {
        TODO("Not yet implemented")
    }

    override suspend fun updateAccount(account: Account) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAccount(account: Account) {
        TODO("Not yet implemented")
    }

    override fun getAccountStream(id: Int): Flow<Account?> {
        TODO("Not yet implemented")
    }

    override fun getAllAccountsStream(): Flow<List<Account>> {
        TODO("Not yet implemented")
    }

    override fun getAccountWithTransactionsStream(id: Int): Flow<AccountWithTransactions> {
        TODO("Not yet implemented")
    }

    override fun getAllAccountsWithTransactionsStream(): Flow<List<AccountWithTransactions>> {
        TODO("Not yet implemented")
    }

    override fun getFullAccountStream(id: Int): Flow<FullAccount> {
        TODO("Not yet implemented")
    }

    override fun getAllFullAccountsStream(): Flow<List<FullAccount>> {
        TODO("Not yet implemented")
    }

    override fun totalBalance(): Flow<Pair<Currency, Float>> {
        TODO("Not yet implemented")
    }

    override suspend fun registerTransfer(transfer: Transfer) {
        TODO("Not yet implemented")
    }
}