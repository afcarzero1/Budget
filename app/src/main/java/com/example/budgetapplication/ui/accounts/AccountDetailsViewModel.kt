package com.example.budgetapplication.ui.accounts

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgetapplication.data.accounts.Account
import com.example.budgetapplication.data.accounts.AccountsRepository
import com.example.budgetapplication.ui.navigation.AccountDetails
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AccountDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val accountsRepository: AccountsRepository
) : ViewModel() {
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    val accountId: Int = checkNotNull(savedStateHandle[AccountDetails.accountIdArg])

    val accountState: StateFlow<AccountDetailsUiState> =
        accountsRepository.getAccountStream(accountId)
            .filterNotNull()
            .map {
                AccountDetailsUiState(it, true)
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = AccountDetailsUiState()
            )

    var accountUiState by mutableStateOf(AccountDetailsUiState())
        private set


    fun updateUiState(account: Account) {
        this.accountUiState = AccountDetailsUiState(
            account = account,
            isValid = validateInput(account)
        )
    }

    private fun validateInput(account: Account): Boolean {
        return with(account) {
            name.isNotBlank() && currency.isNotBlank()
        }
    }

    suspend fun updateAccount() {
        if (accountUiState.isValid) {
            accountsRepository.updateAccount(accountUiState.account)
        }
    }

    suspend fun deleteAccount() {
        accountsRepository.deleteAccount(accountState.value.account)
    }
}


data class AccountDetailsUiState(
    val account: Account = Account(0, "", 0.0f, "USD"),
    val isValid: Boolean = false
)