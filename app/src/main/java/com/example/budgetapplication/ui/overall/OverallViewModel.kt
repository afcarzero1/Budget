package com.example.budgetapplication.ui.overall

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgetapplication.data.accounts.AccountsRepository
import com.example.budgetapplication.data.accounts.FullAccount
import com.example.budgetapplication.data.currencies.Currency
import com.example.budgetapplication.data.transactions.TransactionType
import com.example.budgetapplication.data.transactions.TransactionsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDateTime
import java.time.YearMonth

class OverallViewModel(
    accountsRepository: AccountsRepository,
    transactionsRepository: TransactionsRepository
) : ViewModel(){

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    val accountsUiState: StateFlow<OverallAccountsUiState> = accountsRepository
        .getAllFullAccountsStream()
        .map { OverallAccountsUiState(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = OverallAccountsUiState()
        )

    val accountsTotalBalance: StateFlow<Pair<Currency, Float>> = accountsRepository
        .totalBalance()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = Pair(Currency("USD", 1.0f, LocalDateTime.now()), 0f)
        )

    val lastExpenses: StateFlow<Map<YearMonth,Float>> = transactionsRepository
        .getFullTransactionsByMonthsStream(
            fromDate = YearMonth.now().minusMonths(6),
            toDate = YearMonth.now()
        ).map {
            val monthToTransactions : MutableMap<YearMonth, Float> = mutableMapOf()

            for (transaction in it) {
                val month = YearMonth.of(
                    transaction.transactionRecord.date.year,
                    transaction.transactionRecord.date.month
                )

                // Check if the transaction is an "Expense"
                if (transaction.transactionRecord.type == TransactionType.EXPENSE) {
                    val currentAmount = monthToTransactions[month]
                    val newAmount = currentAmount?.plus(transaction.transactionRecord.amount) ?: transaction.transactionRecord.amount
                    monthToTransactions[month] = newAmount
                }
            }
            monthToTransactions
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = mapOf()
        )


}

data class OverallAccountsUiState(
    val accountsList: List<FullAccount> = listOf()
)

