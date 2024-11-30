package com.example.budgetahead.ui.cashflow

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgetahead.data.balances.BalancesRepository
import com.example.budgetahead.data.categories.Category
import com.example.budgetahead.data.currencies.CurrenciesRepository
import com.example.budgetahead.data.currencies.Currency
import com.example.budgetahead.data.transactions.FullTransactionRecord
import com.example.budgetahead.ui.navigation.CashFlowOverview
import com.example.budgetahead.ui.overall.CashFlow
import com.example.budgetahead.use_cases.ClassifyCategoriesUseCaseImpl
import com.example.budgetahead.use_cases.GroupTransactionsAndTransfersByDateUseCase
import com.example.budgetahead.use_cases.getYearMonth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.LocalDateTime
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

    val dateToShowFlow: MutableStateFlow<YearMonth> = MutableStateFlow(initialDateToShow)

    private val monthExpensesFlow: Flow<Map<YearMonth, Map<Category, Float>>> =
        dateToShowFlow
            .flatMapLatest {
                balancesRepository.getExecutedBalancesByMonthStream(it, it)
            }.map {
                ClassifyCategoriesUseCaseImpl().execute(
                    it,
                    false,
                )
            }

    private val monthIncomesFlow: Flow<Map<YearMonth, Map<Category, Float>>> =
        dateToShowFlow
            .flatMapLatest {
                balancesRepository.getExecutedBalancesByMonthStream(it, it)
            }.map {
                ClassifyCategoriesUseCaseImpl().execute(
                    it,
                    true,
                )
            }

    private val monthExpectedExpenseFlow =
        dateToShowFlow
            .flatMapLatest {
                Log.d("CashflowOverviewViewModel", "Calling get expected (projected) balances by month")
                balancesRepository.getExpectedBalancesByMonthStream(it, it)
            }.map {
                ClassifyCategoriesUseCaseImpl().execute(
                    it,
                    false,
                )
            }

    private val monthExpectedIncomeFlow =
        dateToShowFlow
            .flatMapLatest {
                balancesRepository.getExpectedBalancesByMonthStream(it, it)
            }.map {
                ClassifyCategoriesUseCaseImpl().execute(
                    it,
                    true,
                )
            }

    private val monthPlannedExpenseFlow =
        dateToShowFlow
            .flatMapLatest {
                Log.d("CashflowOverviewViewModel", "Calling get planned balances by month")
                balancesRepository.getPlannedBalancesByMonthStream(it, it)
            }.map {
                ClassifyCategoriesUseCaseImpl().execute(
                    it,
                    false,
                )
            }

    private val monthPlannedIncomeFlow =
        dateToShowFlow
            .flatMapLatest {
                balancesRepository.getPlannedBalancesByMonthStream(it, it)
            }.map {
                ClassifyCategoriesUseCaseImpl().execute(
                    it,
                    true,
                )
            }

    val executedCashFlow =
        combine(
            monthExpensesFlow,
            monthIncomesFlow,
            dateToShowFlow,
            currenciesRepository.getDefaultCurrencyStream().map {
                Currency(
                    it,
                    1.0f,
                    LocalDateTime.now(),
                )
            },
        ) { expenses, incomes, date, currency ->
            CashFlow(
                outgoing = expenses[date]?.values?.sum() ?: 0.0f,
                ingoing = incomes[date]?.values?.sum() ?: 0.0f,
                currency = currency,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue =
                CashFlow(
                    outgoing = 0f,
                    ingoing = 0f,
                    currency = Currency("USD", 1.0f, LocalDateTime.now()),
                ),
        )

    val expectedCashFlow =
        combine(
            monthExpectedExpenseFlow,
            monthExpectedIncomeFlow,
            dateToShowFlow,
            currenciesRepository.getDefaultCurrencyStream().map {
                Currency(
                    it,
                    1.0f,
                    LocalDateTime.now(),
                )
            },
        ) { expenses, incomes, date, currency ->
            CashFlow(
                outgoing = expenses[date]?.values?.sum() ?: 0.0f,
                ingoing = incomes[date]?.values?.sum() ?: 0.0f,
                currency = currency,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue =
                CashFlow(
                    outgoing = 0f,
                    ingoing = 0f,
                    currency = Currency("USD", 1.0f, LocalDateTime.now()),
                ),
        )

    val plannedCashFlow =
        combine(
            monthPlannedExpenseFlow,
            monthPlannedIncomeFlow,
            dateToShowFlow,
            currenciesRepository.getDefaultCurrencyStream().map {
                Currency(
                    it,
                    1.0f,
                    LocalDateTime.now(),
                )
            },
        ) { expenses, incomes, date, currency ->
            Log.d("CashflowOverviewViewModel", "Computing planned cashflow")
            CashFlow(
                outgoing = expenses[date]?.values?.sum() ?: 0.0f,
                ingoing = incomes[date]?.values?.sum() ?: 0.0f,
                currency = currency,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue =
                CashFlow(
                    outgoing = 0f,
                    ingoing = 0f,
                    currency = Currency("USD", 1.0f, LocalDateTime.now()),
                ),
        )

    val pendingTransactions =
        dateToShowFlow
            .flatMapLatest {
                if (it.isBefore(YearMonth.now())) {
                    flow { emit(emptyList<FullTransactionRecord>()) }
                } else if(it.isAfter(YearMonth.now())) {
                    balancesRepository.getPendingTransactions(
                        it.atDay(1),
                        it.atEndOfMonth(),
                    )
                }
                else {
                    Log.d("CashflowOverviewViewModel", "Calling pending transactions")
                    balancesRepository.getPendingTransactions(
                        minOf(LocalDate.now(), it.atEndOfMonth()),
                        it.atEndOfMonth(),
                    )
                }
            }.map {
                Log.d("CashflowOverviewViewModel", "${it}")
                GroupTransactionsAndTransfersByDateUseCase().execute(
                    transactions = it,
                    transfers = listOf(),
                )
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = listOf(),
            )

    val baseCurrency: StateFlow<String> =
        currenciesRepository.getDefaultCurrencyStream().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = "USD",
        )
}
