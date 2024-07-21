package com.example.budgetahead.data.transfers

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgetahead.data.accounts.AccountsRepository
import com.example.budgetahead.data.accounts.FullAccount
import com.example.budgetahead.data.transactions.TransactionsRepository
import com.example.budgetahead.ui.navigation.TransferDetails
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class TransferDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val transactionsRepository: TransactionsRepository,
    private val accountsRepository: AccountsRepository,
) : ViewModel() {
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    private val transferId: Int =
        checkNotNull(savedStateHandle[TransferDetails.transferIdArg])

    val transferDBState: StateFlow<TransferDetailsUiState> =
        transactionsRepository
            .getTransfersStream(transferId)
            .filterNotNull()
            .map {
                TransferDetailsUiState(it, true)
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = TransferDetailsUiState(),
            )

    val accountsListState: StateFlow<List<FullAccount>> =
        accountsRepository
            .getAllFullAccountsStream()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000L),
                initialValue = listOf(),
            )

    var transferUiState by mutableStateOf(TransferDetailsUiState())
        private set

    var showUpdatedState by mutableStateOf(false)
        private set

    // TODO: Understand which approach is best, using this initializer or a
    // TODO: boolean variable stating what to use
    init {
        viewModelScope.launch {
            transferDBState.collect { dbState ->
                updateUiState(dbState.transfer)
            }
        }
    }

    fun updateUiState(transfer: Transfer) {
        this.transferUiState =
            TransferDetailsUiState(
                transfer = transfer,
                isValid = validateInput(transfer),
            )
        showUpdatedState = true
    }

    suspend fun updateTransfer() {
        if (transferUiState.isValid) {
            transactionsRepository.updateTransfer(transferUiState.transfer)
        }
    }

    suspend fun deleteTransfer() {
        transactionsRepository.deleteTransfer(transferDBState.value.transfer)
    }

    // TODO: Move this to a Use case class
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
        val sourceAccount =
            accountsListState.value.find { it.account.id == transfer.sourceAccountId }
        // Check if the account was found and if it has enough balance to transfer
        if (sourceAccount == null || sourceAccount.balance < transfer.amountSource) {
            return false
        }

        return true
    }
}

data class TransferDetailsUiState(
    val transfer: Transfer =
        Transfer(
            id = -1,
            sourceAccountId = -1,
            sourceAccountTransactionId = -1,
            destinationAccountId = -1,
            destinationAccountTransactionId = -1,
            amountDestination = 0f,
            amountSource = 0f,
            date = LocalDateTime.now(),
        ),
    val isValid: Boolean = false,
)
