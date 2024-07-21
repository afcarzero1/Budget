package com.example.budgetahead.data.accounts

import com.example.budgetahead.data.currencies.CurrenciesRepository
import com.example.budgetahead.data.currencies.Currency
import com.example.budgetahead.data.transactions.TransactionsRepository
import com.example.budgetahead.data.transfers.Transfer
import java.time.LocalDateTime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class OfflineAccountsRepository(
    private val accountDao: AccountDao,
    private val currenciesRepository: CurrenciesRepository,
    private val transactionsRepository: TransactionsRepository
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

    override fun totalBalance(): Flow<Pair<Currency, Float>> = combine(
        this.getAllAccountsWithTransactionsStream(),
        currenciesRepository.getDefaultCurrencyStream(),
        currenciesRepository.getAllCurrenciesStream()
    ) { fullAccounts, baseCurrency, currencies ->
        val currencyMap = currencies.associateBy { it.name }
        val currencyToBalanceMap = mutableMapOf<Currency, Float>()

        for (fullAccount in fullAccounts) {
            val currency =
                currencyMap[fullAccount.account.currency] ?: throw IllegalStateException(
                    "Currency not found for name: ${fullAccount.account.currency}"
                )
            val balance = fullAccount.balance

            // Update the total balance for the currency
            currencyToBalanceMap[currency] =
                currencyToBalanceMap.getOrDefault(currency, 0f) + balance
        }

        var totalBalance = 0f
        for ((currency, balance) in currencyToBalanceMap) {
            totalBalance += balance * (1 / currency.value)
        }

        Pair(
            Currency(baseCurrency, 1.0f, LocalDateTime.now()),
            totalBalance
        )
    }

    override suspend fun registerTransfer(transfer: Transfer) =
        transactionsRepository.insertTransfer(transfer)
}
