package com.example.budgetapplication.ui.accounts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgetapplication.data.accounts.Account
import com.example.budgetapplication.data.accounts.AccountsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class AccountsViewModel(accountsRepository: AccountsRepository): ViewModel() {

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    val accountsUiState: StateFlow<AccountsUiState> = accountsRepository
        .getAllAccountsStream()
        .map { AccountsUiState(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = AccountsUiState()
        )


}

data class AccountsUiState(val accountsList: List<Account> = listOf())