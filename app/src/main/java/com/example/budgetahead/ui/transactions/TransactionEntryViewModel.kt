package com.example.budgetahead.ui.transactions

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
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
import java.time.LocalDateTime


class TransactionEntryViewModel(
    private val transactionsRepository: TransactionsRepository,
    private val accountsRepository: AccountsRepository,
    private val categoriesRepository: CategoriesRepository
) : ViewModel() {
    val accountsListState: StateFlow<List<Account>> = accountsRepository
        .getAllAccountsStream()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = listOf()
        )

    val categoriesListState: StateFlow<List<Category>> = categoriesRepository
        .getAllCategoriesStream()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = listOf()
        )

    var transactionUiState by mutableStateOf(TransactionUiState())
        private set

    fun updateUiState(transaction: TransactionRecord) {
        this.transactionUiState = TransactionUiState(
            transaction = transaction,
            isValid = validateInput(transaction)
        )
    }

    private fun validateInput(transaction: TransactionRecord): Boolean {
        return with(transaction) {
             amount > 0 && accountId >= 0 && categoryId != null && categoryId >= 0
        }
    }

    suspend fun saveTransaction() {
        if (validateInput(transactionUiState.transaction)) {
            transactionsRepository.insert(transactionUiState.transaction)
        }
    }
}

data class TransactionUiState(
    val transaction: TransactionRecord = TransactionRecord(
        id = 0,
        name = "",
        type = TransactionType.EXPENSE,
        accountId = -1,
        categoryId = -1,
        amount = 0f,
        date = LocalDateTime.now()
    ),
    val isValid: Boolean = false
)