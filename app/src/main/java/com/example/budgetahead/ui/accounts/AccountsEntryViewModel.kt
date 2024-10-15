package com.example.budgetahead.ui.accounts

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgetahead.data.accounts.Account
import com.example.budgetahead.data.accounts.AccountsRepository
import com.example.budgetahead.data.currencies.CurrenciesRepository
import com.example.budgetahead.data.currencies.Currency
import com.example.budgetahead.ui.components.graphics.AvailableColors
import com.example.budgetahead.ui.components.graphics.convertColorToLong
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class AccountsEntryViewModel(
    private val accountsRepository: AccountsRepository,
    private val currenciesRepository: CurrenciesRepository,
) : ViewModel() {
    var accountUiState by mutableStateOf(AccountUiState())
        private set

    var currenciesListState: StateFlow<List<Currency>> =
        currenciesRepository
            .getAllCurrenciesStream()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000L),
                initialValue = listOf(),
            )

    fun updateUiState(account: Account) {
        Log.d("AccountEntryViewModel", "updateUiState: $account")
        this.accountUiState =
            AccountUiState(
                account = account,
                isValid = validateInput(account),
            )
    }

    private fun validateInput(account: Account): Boolean =
        with(account) {
            name.isNotBlank() &&
                currency.isNotBlank() &&
                currenciesListState
                    .value
                    .map { it.name }
                    .contains(currency)
        }

    suspend fun saveAccount() {
        if (validateInput(accountUiState.account)) {
            accountsRepository.insertAccount(accountUiState.account)
        }
    }
}

data class AccountUiState(
    val account: Account =
        Account(
            id = 0,
            name = "",
            initialBalance = 0f,
            currency = "USD",
            color = convertColorToLong(AvailableColors.colorsList[0]),
        ),
    val isValid: Boolean = false,
)
