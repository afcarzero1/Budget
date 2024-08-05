package com.example.budgetahead.ui.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgetahead.data.future_transactions.FullFutureTransaction
import com.example.budgetahead.data.future_transactions.FutureTransactionsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class FutureTransactionsSummaryViewModel(
    futureTransactionsRepository: FutureTransactionsRepository
) : ViewModel() {
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    val futureTransactionsUiState =
        futureTransactionsRepository
            .getAllFutureFullTransactionsStream()
            .map { FutureTransactionsUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = FutureTransactionsUiState()
            )
}

data class FutureTransactionsUiState(
    val futureTransactionsList: List<FullFutureTransaction> = listOf()
)
