package com.example.budgetahead.ui.accounts

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgetahead.data.accounts.Account
import com.example.budgetahead.data.accounts.AccountsRepository
import com.example.budgetahead.data.accounts.FullAccount
import com.example.budgetahead.data.currencies.Currency
import com.example.budgetahead.ui.navigation.AccountDetails
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDateTime

class AccountSummaryViewModel(
    savedStateHandle: SavedStateHandle,
    private val accountsRepository: AccountsRepository,
) : ViewModel() {

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    val accountId: Int = checkNotNull(savedStateHandle[AccountDetails.accountIdArg])


    val accountState: StateFlow<AccountSummaryUiState> =
        accountsRepository.getFullAccountStream(accountId)
            .filterNotNull()
            .map { AccountSummaryUiState(it) }
            .filterNotNull()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(AccountSummaryViewModel.TIMEOUT_MILLIS),
                initialValue = AccountSummaryUiState()
            )

    //val accountTransactions
}

data class AccountSummaryUiState(
    val accountWithTransactions: FullAccount = FullAccount(
        account = Account(id=0, name="", initialBalance = 0f, currency = "USD"),
        currency = Currency("USD", 1f, LocalDateTime.now()),
        transactionRecords = listOf()
    ),
)