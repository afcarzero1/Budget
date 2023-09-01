package com.example.budgetapplication.ui.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgetapplication.data.accounts.AccountWithTransactions
import com.example.budgetapplication.data.accounts.AccountsRepository
import com.example.budgetapplication.data.categories.CategoriesRepository
import com.example.budgetapplication.data.categories.CategoryWithTransactions
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class CategoriesSummaryViewModel(categoriesRepository: CategoriesRepository): ViewModel() {

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    val categoriesUiState: StateFlow<CategoriesUiState> = categoriesRepository
        .getAllCategoriesWithTransactionsStream()
        .map { CategoriesUiState(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = CategoriesUiState()
        )

}

data class CategoriesUiState(val categoriesList: List<CategoryWithTransactions> = listOf())