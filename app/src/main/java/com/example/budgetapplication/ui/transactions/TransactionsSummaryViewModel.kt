package com.example.budgetapplication.ui.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgetapplication.data.transactions.FullTransactionRecord
import com.example.budgetapplication.data.transactions.TransactionsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class TransactionsSummaryViewModel(transactionsRepository: TransactionsRepository) : ViewModel() {

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    val transactionsUiState = transactionsRepository
        .getAllFullTransactionsStream()
        .map { TransactionsUiState(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = TransactionsUiState()
        )

}

data class TransactionsUiState(val transactionsList: List<FullTransactionRecord> = listOf())