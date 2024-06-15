package com.example.budgetahead.ui.transactions

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgetahead.data.categories.CategoriesRepository
import com.example.budgetahead.data.categories.Category
import com.example.budgetahead.data.currencies.CurrenciesRepository
import com.example.budgetahead.data.currencies.Currency
import com.example.budgetahead.data.future_transactions.FutureTransaction
import com.example.budgetahead.data.future_transactions.FutureTransactionsRepository
import com.example.budgetahead.data.future_transactions.RecurrenceType
import com.example.budgetahead.data.transactions.TransactionType
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDateTime


class FutureTransactionEntryViewModel(
    private val futureTransactionsRepository: FutureTransactionsRepository,
    private val categoriesRepository: CategoriesRepository,
    private val currenciesRepository: CurrenciesRepository
) : ViewModel() {
    val categoriesListState: StateFlow<List<Category>> = categoriesRepository
        .getAllCategoriesStream()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = listOf()
        )

    val currenciesListState: StateFlow<List<Currency>> = currenciesRepository
        .getAllCurrenciesStream()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = listOf()
        )

    var transactionUiState by mutableStateOf(FutureTransactionUiState())
        private set

    fun updateUiState(futureTransaction: FutureTransaction) {
        this.transactionUiState = FutureTransactionUiState(
            futureTransaction = futureTransaction,
            isValid = validateInput(futureTransaction)
        )
    }

    private fun validateInput(futureTransaction: FutureTransaction): Boolean {
        return with(futureTransaction){
            amount > 0 && categoryId >= 0 && endDate > startDate
        }
    }

    suspend fun saveTransaction() {
        if (validateInput(transactionUiState.futureTransaction)) {
            futureTransactionsRepository.insert(transactionUiState.futureTransaction)
        }
    }
}

data class FutureTransactionUiState(
    val futureTransaction: FutureTransaction = FutureTransaction(
        id = 0,
        type = TransactionType.EXPENSE,
        name = "",
        amount = 0f,
        categoryId = -1,
        currency = "USD",
        startDate = LocalDateTime.now(),
        endDate = LocalDateTime.now(),
        recurrenceType = RecurrenceType.MONTHLY,
        recurrenceValue = 1
    ),
    val isValid: Boolean = false
)