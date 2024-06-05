package com.example.budgetapplication.data.accounts

import com.example.budgetapplication.data.currencies.CurrenciesRepository
import com.example.budgetapplication.data.currencies.Currency
import com.example.budgetapplication.data.transactions.TransactionRecord
import com.example.budgetapplication.data.transactions.TransactionType
import com.example.budgetapplication.data.transactions.TransactionsRepository
import com.example.budgetapplication.data.transfers.Transfer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.LocalDateTime

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

            //TODO: Use default currency. Add currency repository and use the actual actual currency
            // for the
            // default currency
            Pair(
                Currency(baseCurrency, 1.0f, LocalDateTime.now()),
                totalBalance
            )
        }
    }

    override suspend fun registerTransfer(transfer: Transfer) {
        val sourceTransaction = TransactionRecord(
            id = 0,
            name = transfer.destinationAccountId.toString(),
            type = TransactionType.EXPENSE_TRANSFER,
            accountId = transfer.sourceAccountId,
            categoryId = null,
            amount = transfer.amountSource,
            date = transfer.date
        )

        val destinationTransaction = TransactionRecord(
            id = 0,  // Auto-generate the ID
            name = transfer.sourceAccountId.toString(),
            type = TransactionType.INCOME_TRANSFER,
            accountId = transfer.destinationAccountId,
            categoryId = null,
            amount = transfer.amountDestination,
            date = transfer.date
        )

        // Insert the destination transaction into the database
        transactionsRepository.insertMany(
            destinationTransaction, sourceTransaction
        )

    }
}