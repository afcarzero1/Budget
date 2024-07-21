package com.example.budgetahead.ui.cashflow

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgetahead.data.balances.BalancesRepository
import com.example.budgetahead.data.categories.Category
import com.example.budgetahead.data.currencies.CurrenciesRepository
import com.example.budgetahead.ui.navigation.CashFlowOverview
import com.example.budgetahead.use_cases.ClassifyCategoriesUseCaseImpl
import com.example.budgetahead.use_cases.getYearMonth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.YearMonth

@OptIn(ExperimentalCoroutinesApi::class)
class CashFlowOverviewViewModel(
    savedStateHandle: SavedStateHandle,
    currenciesRepository: CurrenciesRepository,
    balancesRepository: BalancesRepository,
) : ViewModel() {
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    private val initialDateToShow: YearMonth =
        checkNotNull(savedStateHandle.getYearMonth(CashFlowOverview.dateArg))

    val dateToShowFlow: Flow<YearMonth> = MutableStateFlow(initialDateToShow)

    val monthExpensesFlow: Flow<Map<YearMonth, Map<Category, Float>>> =
        dateToShowFlow
            .flatMapLatest {
                balancesRepository.getCurrentBalancesByMonthStream(it, it)
            }.map {
                ClassifyCategoriesUseCaseImpl().execute(
                    it,
                    false,
                )
            }

    val monthIncomesFlow: Flow<Map<YearMonth, Map<Category, Float>>> =
        dateToShowFlow
            .flatMapLatest {
                balancesRepository.getCurrentBalancesByMonthStream(it, it)
            }.map {
                ClassifyCategoriesUseCaseImpl().execute(
                    it,
                    true,
                )
            }

    val monthExpectedExpenseFlow =
        dateToShowFlow
            .flatMapLatest {
                balancesRepository.getExpectedBalancesByMonthStream(it, it)
            }.map {
                ClassifyCategoriesUseCaseImpl().execute(
                    it,
                    false,
                )
            }

    val balancesByDay =
        dateToShowFlow
            .flatMapLatest {
                balancesRepository.getBalanceByDay(
                    fromDate = it.atDay(1),
                    toDate = it.atEndOfMonth(),
                )
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(CashFlowOverviewViewModel.TIMEOUT_MILLIS),
                initialValue = mapOf(),
            )

    val baseCurrency: StateFlow<String> =
        currenciesRepository.getDefaultCurrencyStream().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(CashFlowOverviewViewModel.TIMEOUT_MILLIS),
            initialValue = "USD",
        )
}
