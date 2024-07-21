package com.example.budgetahead.ui.accounts

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgetahead.data.accounts.Account
import com.example.budgetahead.data.accounts.AccountWithTransactions
import com.example.budgetahead.data.accounts.AccountsRepository
import com.example.budgetahead.data.currencies.CurrenciesRepository
import com.example.budgetahead.data.currencies.Currency
import com.example.budgetahead.ui.navigation.AccountDetails
import com.example.budgetahead.ui.transactions.GroupOfTransactionsAndTransfers
import com.example.budgetahead.use_cases.GroupTransactionsAndTransfersByDateUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDateTime

class AccountSummaryViewModel(
    savedStateHandle: SavedStateHandle,
    accountsRepository: AccountsRepository,
    currenciesRepository: CurrenciesRepository,
) : ViewModel() {
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    val accountId: Int = checkNotNull(savedStateHandle[AccountDetails.accountIdArg])

    val baseCurrency =
        currenciesRepository
            .getDefaultCurrencyStream()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = "USD",
            )

    val accountState: StateFlow<AccountSummaryUiState> =
        accountsRepository
            .getFullAccountStream(accountId)
            .filterNotNull()
            .map { fullAccount ->
                AccountSummaryUiState(
                    accountWithTransactions =
                        AccountWithTransactions(
                            account = fullAccount.account,
                            transactionRecords =
                                fullAccount.transactionRecords.map {
                                    it.transactionRecord
                                },
                        ),
                    currency = fullAccount.currency,
                    transactionsAndTransfers =
                        GroupTransactionsAndTransfersByDateUseCase().execute(
                            fullAccount.transactionRecords.filter { it.category != null },
                            fullAccount.transfersIncoming + fullAccount.transfersOutgoing,
                        ),
                )
            }.filterNotNull()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = AccountSummaryUiState(),
            )
}

data class AccountSummaryUiState(
    val accountWithTransactions: AccountWithTransactions =
        AccountWithTransactions(
            account = Account(id = 0, name = "", initialBalance = 0f, currency = "USD"),
            transactionRecords = listOf(),
        ),
    val currency: Currency = Currency("USD", 1f, LocalDateTime.now()),
    val transactionsAndTransfers: List<GroupOfTransactionsAndTransfers> = listOf(),
)
