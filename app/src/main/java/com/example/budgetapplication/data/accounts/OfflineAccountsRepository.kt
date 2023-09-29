package com.example.budgetapplication.data.accounts

import com.example.budgetapplication.data.currencies.CurrenciesRepository
import com.example.budgetapplication.data.currencies.Currency
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime

class OfflineAccountsRepository(
    private val accountDao: AccountDao,
    private val currenciesRepository: CurrenciesRepository
) : AccountsRepository {
    override suspend fun insertAccount(account: Account) = accountDao.insert(account)

    override suspend fun updateAccount(account: Account) = accountDao.update(account)

    override suspend fun deleteAccount(account: Account) = accountDao.delete(account)

    override fun getAccountStream(id: Int): Flow<Account?> = accountDao.getAccount(id)

    override fun getAllAccountsStream(): Flow<List<Account>> = accountDao.getAllAccounts()

    override fun getAccountWithTransactionsStream(id: Int): Flow<AccountWithTransactions> =
        accountDao.getAccountWithTransactions(id)

    override fun getAllAccountsWithTransactionsStream(): Flow<List<AccountWithTransactions>> =
        accountDao.getAllAccountsWithTransactions()

    override fun getFullAccountStream(id: Int): Flow<FullAccount> = accountDao.getFullAccount(id)

    override fun getAllFullAccountsStream(): Flow<List<FullAccount>> =
        accountDao.getAllFullAccounts()

    override fun totalBalance(): Flow<Pair<Currency, Float>> {
        return combine(
            this.getAllFullAccountsStream(),
            currenciesRepository.getDefaultCurrencyStream()
        ){ fullAccounts, baseCurrency ->
            val currencyToBalanceMap = mutableMapOf<Currency, Float>()

            for (fullAccount in fullAccounts) {
                val currency = fullAccount.currency
                val balance = fullAccount.balance

                // Update the total balance for the currency
                currencyToBalanceMap[currency] =
                    currencyToBalanceMap.getOrDefault(currency, 0f) + balance
            }

            var totalBalance = 0f
            for ((currency, balance) in currencyToBalanceMap) {
                totalBalance += balance * (1/currency.value)
            }

            //TODO: Use default currency. Add currency repository and use the actual object for the
            // default currency
            Pair(
                Currency(baseCurrency, 1.0f, LocalDateTime.now()),
                totalBalance
            )
        }
    }
}