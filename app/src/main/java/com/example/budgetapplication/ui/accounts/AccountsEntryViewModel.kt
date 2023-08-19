package com.example.budgetapplication.ui.accounts

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgetapplication.data.accounts.Account
import com.example.budgetapplication.data.accounts.AccountsRepository
import com.example.budgetapplication.data.currencies.CurrenciesRepository
import com.example.budgetapplication.data.currencies.Currency
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
class AccountsEntryViewModel(
    private val accountsRepository: AccountsRepository,
    private val currenciesRepository: CurrenciesRepository
) : ViewModel() {

    var accountUiState by mutableStateOf(AccountUiState())
        private set

    var currenciesListState: StateFlow<List<Currency>> = currenciesRepository
        .getAllCurrenciesStream()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = listOf()
        )


    fun updateUiState(account: Account) {
        Log.d("AccountEntryViewModel", "updateUiState: $account")
        this.accountUiState = AccountUiState(
            account = account,
            isValid = validateInput(account)
        )
    }

    private fun validateInput(account: Account): Boolean {
        return with(account) {
            name.isNotBlank() && currency.isNotBlank() && currenciesListState
                .value
                .map { it.name }
                .contains(currency)
        }
    }

    suspend fun saveAccount() {
        if (validateInput(accountUiState.account)) {
            accountsRepository.insertAccount(accountUiState.account)
        }
    }
}


data class AccountUiState(
    val account: Account = Account(
        id = 0,
        name = "",
        initialBalance = 0f,
        currency = "USD",
        color = 0
    ),
    val isValid: Boolean = false,
)