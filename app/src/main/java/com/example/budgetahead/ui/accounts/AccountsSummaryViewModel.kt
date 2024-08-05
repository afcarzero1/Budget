package com.example.budgetahead.ui.accounts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgetahead.data.accounts.AccountsRepository
import com.example.budgetahead.data.accounts.FullAccount
import com.example.budgetahead.data.currencies.Currency
import com.example.budgetahead.ui.components.ColorAssigner
import com.example.budgetahead.ui.components.graphics.AvailableColors
import java.time.LocalDateTime
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class AccountsViewModel(accountsRepository: AccountsRepository) : ViewModel() {
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    val accountsTotalBalance: StateFlow<Pair<Currency, Float>> =
        accountsRepository
            .totalBalance()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = Pair(Currency("USD", 1.0f, LocalDateTime.now()), 0f)
            )

    val accountsColorAssigner: ColorAssigner =
        ColorAssigner(
            AvailableColors.colorsList
        )

    val accountsUiState: StateFlow<AccountsUiState> =
        accountsRepository
            .getAllFullAccountsStream()
            .map { AccountsUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = AccountsUiState()
            )
}

data class AccountsUiState(val accountsList: List<FullAccount> = listOf())
