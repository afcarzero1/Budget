package com.example.budgetapplication.ui.transactions

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgetapplication.data.currencies.CurrenciesRepository
import com.example.budgetapplication.data.transactions.FullTransactionRecord
import com.example.budgetapplication.data.transactions.TransactionsRepository
import com.example.budgetapplication.data.transfers.Transfer
import com.example.budgetapplication.data.transfers.TransferWithAccounts
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate

class TransactionsSummaryViewModel(
    transactionsRepository: TransactionsRepository,
    currenciesRepository: CurrenciesRepository
) : ViewModel() {

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    val transactionsUiState = combine(
        transactionsRepository.getAllFullTransactionsStream(),
        transactionsRepository.getAllTransfersWithAccountsStream()
    ) { transactions, transfers ->
        // Group transactions and transfers by date
        Log.d("VIEW MODEL TRANSACTIONS", "Transactions: ${transactions.count()}, Transfers: ${transfers.count()}")
        val transactionsGroupedByDate =
            transactions.groupBy { it.transactionRecord.date.toLocalDate() }
        val transfersGroupedByDate = transfers.groupBy { it.transfer.date.toLocalDate() }

        // Create a list to hold the combined data
        val combinedList = mutableListOf<GroupOfTransactionsAndTransfers>()

        // Create a set of all unique dates from both groups, then sort them
        val allDates = (transactionsGroupedByDate.keys union transfersGroupedByDate.keys).sorted().reversed()


        for (date in allDates) {
            val dailyTransactions = transactionsGroupedByDate[date] ?: emptyList()
            val dailyTransfers = transfersGroupedByDate[date] ?: emptyList()
            combinedList.add(
                GroupOfTransactionsAndTransfers(
                    transactions = dailyTransactions,
                    transfers = dailyTransfers,
                    date = date
                )
            )
        }

        TransactionsUiState(
            combinedList
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = TransactionsUiState()
    )

    val baseCurrency = currenciesRepository.getDefaultCurrencyStream()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = "USD"
        )

    var onFutureTransactionsScreen by mutableStateOf(false)
        private set

    fun toggleScreen(){
        onFutureTransactionsScreen = !onFutureTransactionsScreen
    }

    fun toggleScreen(onFutureTransactions: Boolean){
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