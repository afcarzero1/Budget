package com.example.budgetapplication.ui.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgetapplication.data.categories.CategoriesRepository
import com.example.budgetapplication.data.categories.Category
import com.example.budgetapplication.data.categories.CategoryWithTransactions
import com.example.budgetapplication.data.currencies.CurrenciesRepository
import com.example.budgetapplication.ui.components.ColorAssigner
import com.example.budgetapplication.ui.components.graphics.AvailableColors
import com.example.budgetapplication.use_cases.ComputeDeltaFromTransactionsUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.YearMonth

class CategoriesSummaryViewModel(
    categoriesRepository: CategoriesRepository,
    currenciesRepository: CurrenciesRepository,
) : ViewModel() {

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    private val monthOfTransactions: MutableStateFlow<YearMonth> = MutableStateFlow(YearMonth.now())


    val colorAssigner = ColorAssigner(
        AvailableColors.colorsList
    )


    val baseCurrency = currenciesRepository.getDefaultCurrencyStream().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = "USD",
        )


    val currentMonthOfTransactions: StateFlow<YearMonth> = monthOfTransactions.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = YearMonth.now()
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val categoriesUiState: StateFlow<CategoriesUiState> = currentMonthOfTransactions.flatMapLatest {
            categoriesRepository.getAllCategoriesWithTransactionsStream(
                it.atDay(1).atStartOfDay(), it.atEndOfMonth().atTime(23, 59, 59)
            )
        }.map { categoryWithTransactionsList ->
            val deltaUseCase = ComputeDeltaFromTransactionsUseCase()
            CategoriesUiState(categoryWithTransactionsList,
                categoryWithTransactionsList.associate { categoryWithTransactions ->
                    Pair(
                        categoryWithTransactions.category,
                        deltaUseCase.computeDelta(categoryWithTransactions.transactions.map {
                            Pair(it.transactionRecord, it.account.currency)
                        })
                    )
                })
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = CategoriesUiState()
        )

    fun setMonthOfTransactions(date: YearMonth) {
        monthOfTransactions.value = date
    }

}

data class CategoriesUiState(
    val categoriesList: List<CategoryWithTransactions> = listOf(),
    val categoriesDelta: Map<Category, Float> = mapOf()
)