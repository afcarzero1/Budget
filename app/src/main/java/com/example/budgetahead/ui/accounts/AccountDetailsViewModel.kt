package com.example.budgetahead.ui.accounts

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgetahead.data.accounts.Account
import com.example.budgetahead.data.accounts.AccountsRepository
import com.example.budgetahead.ui.navigation.AccountDetails
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class AccountDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val accountsRepository: AccountsRepository,
) : ViewModel() {
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    val accountId: Int = checkNotNull(savedStateHandle[AccountDetails.accountIdArg])

    val accountState: StateFlow<AccountDetailsUiState> =
        accountsRepository
            .getAccountStream(accountId)
            .filterNotNull()
            .map {
                AccountDetailsUiState(it, true)
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = AccountDetailsUiState(),
            )

    var accountUiState by mutableStateOf(AccountDetailsUiState())
        private set

    var showUpdatedState by mutableStateOf(false)
        private set

    fun updateUiState(account: Account) {
        Log.d("ACCOUNT VIEW MODEL", "The id is : $accountId")
        Log.d("ACCOUNT VIEW MODEL", "The updated account is : $account")
        if (account.id != accountId) {
            return
        }

        this.accountUiState =
            AccountDetailsUiState(
                account = account,
                isValid = validateInput(account),
            )
        showUpdatedState = true
    }

    private fun validateInput(account: Account): Boolean =
        with(account) {
            name.isNotBlank() && currency.isNotBlank()
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
    val account: Account = Account(-1, "", 0.0f, "USD"),
    val isValid: Boolean = false,
)
