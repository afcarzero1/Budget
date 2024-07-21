package com.example.budgetahead.ui.categories

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgetahead.data.categories.CategoriesRepository
import com.example.budgetahead.data.categories.Category
import com.example.budgetahead.data.categories.CategoryType
import com.example.budgetahead.data.currencies.CurrenciesRepository
import com.example.budgetahead.data.future_transactions.FullFutureTransaction
import com.example.budgetahead.data.transactions.FullTransactionRecord
import com.example.budgetahead.ui.navigation.CategoryOverview
import com.example.budgetahead.ui.transactions.GroupOfTransactionsAndTransfers
import com.example.budgetahead.use_cases.GroupTransactionsAndTransfersByDateUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class CategoryOverviewViewModel(
    savedStateHandle: SavedStateHandle,
    categoriesRepository: CategoriesRepository,
    currenciesRepository: CurrenciesRepository,
) : ViewModel() {
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    val categoryId: Int = checkNotNull(savedStateHandle[CategoryOverview.categoryIdArg])

    val baseCurrency: StateFlow<String> =
        currenciesRepository
            .getDefaultCurrencyStream()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = "USD",
            )

    private val categoryWithTransactionsFlow =
        categoriesRepository.getCategoryWithTransactionsStream(
            categoryId,
        )
    private val categoryWithPlannedFlow =
        categoriesRepository.getCategoryWithPlannedTransactionsStream(
            categoryId,
        )

    val categoryState: StateFlow<CategorySummaryUiState> =
        combine(
            categoryWithTransactionsFlow,
            categoryWithPlannedFlow,
        ) {
                categoryWithTransactions,
                categoryWithPlanned,
            ->
            CategorySummaryUiState(
                category = categoryWithTransactions.category,
                transactions =
                    GroupTransactionsAndTransfersByDateUseCase().execute(
                        transactions =
                            categoryWithTransactions.transactions.map {
                                FullTransactionRecord(
                                    transactionRecord = it.transactionRecord,
                                    account = it.account,
                                    category = categoryWithTransactions.category,
                                )
                            },
                        transfers = listOf(),
                    ),
                plannedTransactions = categoryWithPlanned.transactions,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue =
                CategorySummaryUiState(
                    category =
                        Category(
                            id = 0,
                            name = "Category",
                            CategoryType.Expense,
                            parentCategoryId = null,
                            iconResId = null,
                        ),
                    transactions = listOf(),
                    plannedTransactions = listOf(),
                ),
        )
}

data class CategorySummaryUiState(
    val category: Category,
    val transactions: List<GroupOfTransactionsAndTransfers> = listOf(),
    val plannedTransactions: List<FullFutureTransaction> = listOf(),
)
