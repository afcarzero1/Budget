package com.example.budgetapplication.ui.transactions

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgetapplication.data.future_transactions.FutureTransaction
import com.example.budgetapplication.data.future_transactions.FutureTransactionsRepository
import com.example.budgetapplication.data.future_transactions.RecurrenceType
import com.example.budgetapplication.data.transactions.TransactionType
import com.example.budgetapplication.ui.navigation.FutureTransactionDetails
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDateTime

class FutureTransactionDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val futureTransactionsRepository: FutureTransactionsRepository
) : ViewModel() {


    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    val futureTransactionId: Int =
        checkNotNull(savedStateHandle[FutureTransactionDetails.futureTransactionIdArg])

    var transactionState: StateFlow<FutureTransactionDetailsUiState> =
        futureTransactionsRepository.getFutureTransactionStream(futureTransactionId)
            .filterNotNull()
            .map {
                FutureTransactionDetailsUiState(it, true)
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = FutureTransactionDetailsUiState()
            )

    var transactionUiState by mutableStateOf(FutureTransactionDetailsUiState())
        private set

    var showUpdatedState by mutableStateOf(false)
        private set

    fun updateUiState(futureTransaction: FutureTransaction) {

        if (futureTransaction.id != futureTransactionId) {
            return
        }

        this.transactionUiState = FutureTransactionDetailsUiState(
            transaction = futureTransaction,
            isValid = validateInput(futureTransaction)
        )
        showUpdatedState = true
    }

    private fun validateInput(futureTransaction: FutureTransaction): Boolean {
        return with(futureTransaction) {
            amount > 0 && categoryId >= 0 && endDate > startDate
        }
    }

    suspend fun updateTransaction() {
        if (transactionUiState.isValid) {
            futureTransactionsRepository.update(transactionUiState.transaction)
        }
    }

    suspend fun deleteTransaction() {
        futureTransactionsRepository.delete(transactionState.value.transaction)
    }
}

data class FutureTransactionDetailsUiState(
    val transaction: FutureTransaction = FutureTransaction(
        id = 0,
        name = "",
        type = TransactionType.EXPENSE,
        categoryId = -1,
        amount = 0f,
        currency = "USD",
        startDate = LocalDateTime.now(),
        endDate = LocalDateTime.now(),
        recurrenceValue = 0,
        recurrenceType = RecurrenceType.NONE
    ),
    val isValid: Boolean = false
)