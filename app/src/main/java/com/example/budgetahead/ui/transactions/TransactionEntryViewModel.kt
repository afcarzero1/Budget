package com.example.budgetahead.ui.transactions

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgetahead.data.accounts.Account
import com.example.budgetahead.data.accounts.AccountsRepository
import com.example.budgetahead.data.categories.CategoriesRepository
import com.example.budgetahead.data.categories.Category
import com.example.budgetahead.data.transactions.TransactionRecord
import com.example.budgetahead.data.transactions.TransactionType
import com.example.budgetahead.data.transactions.TransactionsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class TransactionEntryViewModel(
    private val transactionsRepository: TransactionsRepository,
    private val accountsRepository: AccountsRepository,
    private val categoriesRepository: CategoriesRepository,
) : ViewModel() {
    val accountsListState: StateFlow<List<Account>> =
        accountsRepository
            .getAllAccountsStream()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000L),
                initialValue = listOf(),
            )

    init {
        // Automatically set the first account when the accounts are loaded
        viewModelScope.launch {
            accountsListState.collect { accounts ->
                if (accounts.isNotEmpty() && transactionUiState.transaction.accountId == -1) {
                    // Set the first account id in the UI state
                    transactionUiState =
                        TransactionUiState(
                            transactionUiState.transaction.copy(accountId = accounts.first().id),
                            transactionUiState.isValid,
                        )
                }
            }
        }
    }

    val categoriesListState: StateFlow<List<Category>> =
        categoriesRepository
            .getAllCategoriesStream()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000L),
                initialValue = listOf(),
            )

    var transactionUiState by mutableStateOf(TransactionUiState())
        private set

    fun updateUiState(transaction: TransactionRecord) {
        this.transactionUiState =
            TransactionUiState(
                transaction = transaction,
                isValid = validateInput(transaction),
            )
    }

    private fun validateInput(transaction: TransactionRecord): Boolean =
        with(transaction) {
            amount > 0 && accountId >= 0 && categoryId != null && categoryId >= 0
        }

    suspend fun saveTransaction() {
        if (validateInput(transactionUiState.transaction)) {
            transactionsRepository.insert(transactionUiState.transaction)
        }
    }
}

data class TransactionUiState(
    val transaction: TransactionRecord =
        TransactionRecord(
            id = 0,
            name = "",
            type = TransactionType.EXPENSE,
            accountId = -1,
            categoryId = -1,
            amount = 0f,
            date = LocalDateTime.now(),
        ),
    val isValid: Boolean = false,
)
