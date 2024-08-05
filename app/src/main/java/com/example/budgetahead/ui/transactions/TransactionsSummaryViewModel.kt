package com.example.budgetahead.ui.transactions

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgetahead.data.currencies.CurrenciesRepository
import com.example.budgetahead.data.transactions.FullTransactionRecord
import com.example.budgetahead.data.transactions.TransactionsRepository
import com.example.budgetahead.data.transfers.TransferWithAccounts
import com.example.budgetahead.use_cases.GroupTransactionsAndTransfersByDateUseCase
import java.time.LocalDate
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class TransactionsSummaryViewModel(
    transactionsRepository: TransactionsRepository,
    currenciesRepository: CurrenciesRepository
) : ViewModel() {
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    val transactionsUiState =
        combine(
            transactionsRepository.getAllFullTransactionsStream(),
            transactionsRepository.getAllTransfersWithAccountsStream()
        ) { transactions, transfers ->
            TransactionsUiState(
                GroupTransactionsAndTransfersByDateUseCase().execute(transactions, transfers)
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = TransactionsUiState()
        )

    val baseCurrency =
        currenciesRepository
            .getDefaultCurrencyStream()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = "USD"
            )

    var onFutureTransactionsScreen by mutableStateOf(false)
        private set

    fun toggleScreen() {
        onFutureTransactionsScreen = !onFutureTransactionsScreen
    }

    fun toggleScreen(onFutureTransactions: Boolean) {
        onFutureTransactionsScreen = onFutureTransactions
    }
}

data class GroupOfTransactionsAndTransfers(
    val transactions: List<FullTransactionRecord>,
    val transfers: List<TransferWithAccounts>,
    val date: LocalDate
)

data class TransactionsUiState(
    val groupedTransactionsAndTransfers: List<GroupOfTransactionsAndTransfers> = listOf()
)
