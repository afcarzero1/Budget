package com.example.budgetapplication.ui.transactions


import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.example.budgetapplication.data.transactions.TransactionRecord
import com.example.budgetapplication.data.transactions.TransactionType
import com.example.budgetapplication.data.transactions.TransactionsRepository
import com.example.budgetapplication.ui.navigation.TransactionDetails
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDateTime

class TransactionDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val transactionsRepository: TransactionsRepository
) : ViewModel() {
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    val transactionId: Int = checkNotNull(savedStateHandle[TransactionDetails.transactionIdArg])

    var transactionState: StateFlow<TransactionDetailsUiState> =
        transactionsRepository.getTransactionStream(transactionId)
            .filterNotNull()
            .map {
                TransactionDetailsUiState(it, true)
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = TransactionDetailsUiState()
            )

    var transactionUiState by mutableStateOf(TransactionDetailsUiState())
        private set

    fun updateUiState(transaction: TransactionRecord) {
        this.transactionUiState = TransactionDetailsUiState(
            transaction = transaction,
            isValid = validateInput(transaction)
        )
    }

    private fun validateInput(transaction: TransactionRecord): Boolean {
        return with(transaction) {
            amount > 0 && accountId >= 0 && categoryId != null && categoryId >= 0
        }
    }

    suspend fun updateTransaction() {
        if (transactionUiState.isValid) {
            transactionsRepository.update(transactionUiState.transaction)
        }
    }

    suspend fun deleteTransaction() {
        transactionsRepository.delete(transactionState.value.transaction)
    }
}

data class TransactionDetailsUiState(
    val transaction: TransactionRecord = TransactionRecord(
        id = 0,
        accountId = -1,
        categoryId = -1,
        amount = 0f,
        date = LocalDateTime.now(),
        name = "",
        type = TransactionType.EXPENSE
    ),
    val isValid: Boolean = false
)
