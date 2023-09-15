package com.example.budgetapplication.ui.transactions

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgetapplication.data.categories.CategoriesRepository
import com.example.budgetapplication.data.categories.Category
import com.example.budgetapplication.data.future_transactions.FutureTransaction
import com.example.budgetapplication.data.future_transactions.FutureTransactionsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDateTime


class FutureTransactionEntryViewModel(
    private val futureTransactionsRepository: FutureTransactionsRepository,
    private val categoriesRepository: CategoriesRepository
) : ViewModel() {
    val categoriesListState: StateFlow<List<Category>> = categoriesRepository
        .getAllCategoriesStream()
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
            type.isNotBlank() && amount > 0 && categoryId >= 0
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
        id = -1,
        type = "",
        name = "",
        amount = 0f,
        categoryId = -1,
        startDate = LocalDateTime.now(),
        endDate = LocalDateTime.now(),
        recurrenceType = "",
        recurrenceValue = 0
    ),
    val isValid: Boolean = false
)