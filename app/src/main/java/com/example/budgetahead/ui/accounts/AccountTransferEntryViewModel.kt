package com.example.budgetahead.ui.accounts

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.example.budgetahead.data.accounts.AccountsRepository
import com.example.budgetahead.data.accounts.FullAccount
import com.example.budgetahead.data.transfers.Transfer
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDateTime

class AccountTransferEntryViewModel(
    private val accountsRepository: AccountsRepository
) :
    ViewModel() {
    var transferUiState by mutableStateOf(AccountTransferUiState())
        private set

    val accountsListState: StateFlow<List<FullAccount>> =
        accountsRepository.getAllFullAccountsStream()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000L),
                initialValue = listOf()
            )

    fun updateUiState(transfer: Transfer) {
        this.transferUiState = AccountTransferUiState(
            transfer = transfer,
            isValid = validateInput(transfer)
        )
    }

    suspend fun saveTransfer() {
        if (validateInput(transferUiState.transfer)) {
            accountsRepository.registerTransfer(transfer = transferUiState.transfer)
        }
    }

    private fun validateInput(transfer: Transfer): Boolean {
        // Check that both ids are not null and not the same
        if (transfer.sourceAccountId == -1 || transfer.destinationAccountId == -1) {
            return false
        }
        if (transfer.sourceAccountId == transfer.destinationAccountId) {
            return false
        }

        val currentAccountIdsList = accountsListState.value.map { it.account.id }
        if (!currentAccountIdsList.contains(transfer.sourceAccountId)) {
            return false
        }
        if (!currentAccountIdsList.contains(transfer.destinationAccountId)) {
            return false
        }

        // Chech that amount is not null
        if (transfer.amountSource == 0f || transfer.amountDestination == 0f) {
            return false
        }

        // Check that the account has enough balance to transfer
        // Select the account with the source id
        val sourceAccount = accountsListState.value.find { it.account.id == transfer.sourceAccountId }
        // Check if the account was found and if it has enough balance to transfer
        if (sourceAccount == null || sourceAccount.balance < transfer.amountSource) {
            return false
        }

        return true
    }

}

data class AccountTransferUiState(
    val transfer: Transfer = Transfer(
        id = 0,
        sourceAccountId = -1,
        sourceAccountTransactionId = -1,
        destinationAccountId = -1,
        destinationAccountTransactionId = -1,
        amountDestination = 0f,
        amountSource = 0f,
        date = LocalDateTime.now()
    ),
    val isValid: Boolean = false
)
