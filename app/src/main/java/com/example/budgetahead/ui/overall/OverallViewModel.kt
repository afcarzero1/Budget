package com.example.budgetahead.ui.overall

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgetahead.data.accounts.AccountsRepository
import com.example.budgetahead.data.balances.BalancesRepository
import com.example.budgetahead.data.categories.Category
import com.example.budgetahead.data.currencies.CurrenciesRepository
import com.example.budgetahead.data.currencies.Currency
import com.example.budgetahead.use_cases.ClassifyCategoriesUseCaseImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import kotlin.math.max
import kotlin.math.min

@OptIn(ExperimentalCoroutinesApi::class)
class OverallViewModel(
    accountsRepository: AccountsRepository,
    balancesRepository: BalancesRepository,
    currenciesRepository: CurrenciesRepository,
) : ViewModel() {
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    val baseCurrency: StateFlow<String> =
        currenciesRepository.getDefaultCurrencyStream().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = "USD",
        )

    val accountsTotalBalance: StateFlow<Pair<Currency, Float>> =
        accountsRepository.totalBalance().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = Pair(Currency("USD", 1.0f, LocalDateTime.now()), 0f),
        )

    val centralDateFlow: MutableStateFlow<YearMonth> = MutableStateFlow(YearMonth.now())

    private val currentDateRangeFlow: Flow<Pair<YearMonth, YearMonth>> =
        centralDateFlow.map {
            Pair(it.minusMonths(5), it)
        }

    val currentDateRange: StateFlow<Pair<YearMonth, YearMonth>> =
        currentDateRangeFlow.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = Pair(YearMonth.now().minusMonths(5), YearMonth.now()),
        )

    fun setCentralDate(date: YearMonth) {
        centralDateFlow.value = date
    }

    private val lastExpensesFlow =
        currentDateRangeFlow
            .flatMapLatest { (fromDate, toDate) ->
                balancesRepository.getCurrentBalancesByMonthStream(fromDate, toDate)
            }.map { monthMap ->
                ClassifyCategoriesUseCaseImpl().execute(
                    monthMap,
                    false,
                )
            }

    val lastExpenses: StateFlow<Map<YearMonth, Map<Category, Float>>> =
        lastExpensesFlow
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = mapOf(),
            )

    private val lastIncomesFlow =
        currentDateRangeFlow
            .flatMapLatest { (fromDate, toDate) ->
                balancesRepository.getCurrentBalancesByMonthStream(fromDate, toDate)
            }.map { monthMap ->
                ClassifyCategoriesUseCaseImpl().execute(
                    monthMap,
                    true,
                )
            }

    val lastIncomes: StateFlow<Map<YearMonth, Map<Category, Float>>> =
        lastIncomesFlow
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = mapOf(),
            )

    private val expectedDateFromCurrent: Flow<Pair<YearMonth, YearMonth>> =
        currentDateRangeFlow.map { interval ->
            Pair(interval.second, interval.second.plusMonths(5))
        }

    val expectedDateRange: StateFlow<Pair<YearMonth, YearMonth>> =
        expectedDateFromCurrent.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = Pair(YearMonth.now(), YearMonth.now().plusMonths(5)),
        )

    private val expectedExpensesFlow: Flow<Map<YearMonth, Map<Category, Float>>> =
        expectedDateFromCurrent
            .flatMapLatest { (fromDate, toDate) ->
                balancesRepository.getExpectedBalancesByMonthStream(fromDate, toDate)
            }.map { data ->
                ClassifyCategoriesUseCaseImpl().execute(
                    data,
                    false,
                )
            }

    val expectedExpenses: StateFlow<Map<YearMonth, Map<Category, Float>>> =
        expectedExpensesFlow.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = mapOf(),
        )

    private val expectedIncomesFlow: Flow<Map<YearMonth, Map<Category, Float>>> =
        expectedDateFromCurrent
            .flatMapLatest { (fromDate, toDate) ->
                balancesRepository.getExpectedBalancesByMonthStream(fromDate, toDate)
            }.map { data ->
                ClassifyCategoriesUseCaseImpl().execute(
                    data,
                    true,
                )
            }

    val expectedIncomes: StateFlow<Map<YearMonth, Map<Category, Float>>> =
        expectedIncomesFlow.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = mapOf(),
        )

    private val balanceFromDateFlow: MutableStateFlow<YearMonth> =
        MutableStateFlow(YearMonth.now().minusMonths(0))
    private val balanceToDateFlow: MutableStateFlow<YearMonth> =
        MutableStateFlow(YearMonth.now().plusMonths(6))

    private val balanceDateRangeFlow: Flow<Pair<YearMonth, YearMonth>> =
        combine(
            balanceFromDateFlow,
            balanceToDateFlow,
        ) { fromDate, toDate ->
            Pair(fromDate, toDate)
        }
    val balanceDateRange: StateFlow<Pair<YearMonth, YearMonth>> =
        balanceDateRangeFlow.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = Pair(YearMonth.now().minusMonths(1), YearMonth.now().plusMonths(2)),
        )

    fun setBalanceRangeFlow(
        fromDate: YearMonth,
        toDate: YearMonth,
    ) {
        if (fromDate < toDate) {
            balanceFromDateFlow.value = fromDate
            balanceToDateFlow.value = toDate
        }
    }

    val balancesByDay: StateFlow<Map<LocalDate, Float>> =
        balanceDateRangeFlow
            .flatMapLatest { (fromDate, toDate) ->
                val sundays =
                    generateSequence(fromDate.atDay(1)) { it.plusMonths(1) }
                        .takeWhile { it <= toDate.atEndOfMonth() }
                        .flatMap { monthStart ->
                            generateSequence(monthStart) { it.plusDays(1) }.takeWhile {
                                it.month ==
                                    monthStart.month
                            }
                        }.filter { it.dayOfWeek == DayOfWeek.SUNDAY }

                balancesRepository
                    .getBalanceByDay(
                        fromDate = fromDate.atDay(1),
                        toDate = toDate.atEndOfMonth(),
                    ).map {
                        val newMap: MutableMap<LocalDate, Float> = mutableMapOf()
                        for (day in sundays) {
                            newMap[day] = it[day] ?: 0f // This will NEVER be null
                        }
                        newMap
                    }
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = mapOf(),
            )

    val monthCashFlow =
        combine(
            lastExpenses,
            lastIncomes,
            centralDateFlow,
            currenciesRepository.getDefaultCurrencyStream().map {
                Currency(
                    it,
                    1.0f,
                    LocalDateTime.now(),
                )
            },
        ) { expensesByMonth, incomesByMonth, centralDate, baseCurrency ->

            val centralDateExpenses = expensesByMonth[centralDate]?.values?.sum() ?: 0.0f
            val centralDateIncomes = incomesByMonth[centralDate]?.values?.sum() ?: 0.0f

            // Create the CashFlow object with the summed values and the base currency
            CashFlow(
                outgoing = centralDateExpenses,
                ingoing = centralDateIncomes,
                currency = baseCurrency,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = CashFlow(0f, 0f, Currency("USD", 1.0f, LocalDateTime.now())),
        )

    val monthExpectedExtraCashFlow =
        combine(
            lastExpensesFlow,
            lastIncomesFlow,
            expectedExpensesFlow,
            expectedIncomesFlow,
            centralDateFlow.map { it },
        ) {
                expensesByMonth,
                incomesByMonth,
                expectedExpensesByMonth,
                expectedIncomesByMonth,
                centralDate,
            ->

            val currentMonthExpenses = expensesByMonth[centralDate] ?: emptyMap()
            val currentMonthExpectedExpenses = expectedExpensesByMonth[centralDate]

            val expectedExtraExpense =
                calculateExtraCashFlow(
                    currentMonthExpenses,
                    currentMonthExpectedExpenses,
                    expense = true,
                )

            val expectedExtraIncome =
                calculateExtraCashFlow(
                    incomesByMonth[centralDate] ?: emptyMap(),
                    expectedIncomesByMonth[centralDate],
                    expense = false,
                )

            Pair(expectedExtraExpense, expectedExtraIncome)
        }.combine(
            currenciesRepository.getDefaultCurrencyStream().map {
                Currency(
                    it,
                    1.0f,
                    LocalDateTime.now(),
                )
            },
        ) { cashflow, currency ->
            CashFlow(outgoing = cashflow.first, ingoing = cashflow.second, currency = currency)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = CashFlow(0f, 0f, Currency("USD", 1.0f, LocalDateTime.now())),
        )

    private fun calculateExtraCashFlow(
        actual: Map<Category, Float>,
        expected: Map<Category, Float>?,
        expense: Boolean,
    ): Float {
        var extraCashFlow = 0f
        expected?.let {
            for ((category, expectedAmountOfCategory) in it) {
                val actualAmount = actual.getOrDefault(category, 0f)
                extraCashFlow +=
                    if (expense) {
                        // If we overspent we just dont have actually plannes anymore expenses for this category
                        min(expectedAmountOfCategory - actualAmount, 0f)
                    } else {
                        // If we received extra money then we do not have extra planned!
                        max(expectedAmountOfCategory - actualAmount, 0f)
                    }
            }
        }
        return extraCashFlow
    }
}

data class CashFlow(
    val outgoing: Float,
    val ingoing: Float,
    val currency: Currency,
)
