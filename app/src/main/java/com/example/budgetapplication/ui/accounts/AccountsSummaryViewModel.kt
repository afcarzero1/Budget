package com.example.budgetapplication.ui.accounts

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgetapplication.data.accounts.AccountWithTransactions
import com.example.budgetapplication.data.accounts.AccountsRepository
import com.example.budgetapplication.data.accounts.FullAccount
import com.example.budgetapplication.data.currencies.Currency
import com.example.budgetapplication.ui.components.ColorAssigner
import com.example.budgetapplication.ui.overall.OverallViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDateTime

class AccountsViewModel(accountsRepository: AccountsRepository): ViewModel() {

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    val accountsTotalBalance: StateFlow<Pair<Currency, Float>> = accountsRepository
        .totalBalance()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = Pair(Currency("USD", 1.0f, LocalDateTime.now()), 0f)
        )

    val accountsColorAssigner: ColorAssigner = ColorAssigner(
        listOf(
            Color(0xFFBB86FC),
            Color(0xFF6200EE),
            Color(0xFF03DAC5),
            Color(0xFF007BFF),
            Color(0xFF5C6BC0),
            Color(0xFFE91E63),
            Color(0xFF9C27B0),
            Color(0xFF2196F3),
            Color(0xFF4CAF50),
        )
    )

    val accountsUiState: StateFlow<AccountsUiState> = accountsRepository
        .getAllFullAccountsStream()
        .map { AccountsUiState(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = AccountsUiState()
        )


}

data class AccountsUiState(val accountsList: List<FullAccount> = listOf())