package com.example.budgetahead.ui.overall

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgetahead.data.accounts.AccountsRepository
import com.example.budgetahead.data.accounts.FullAccount
import com.example.budgetahead.data.balances.BalancesRepository
import com.example.budgetahead.data.categories.Category
import com.example.budgetahead.data.currencies.CurrenciesRepository
import com.example.budgetahead.data.currencies.Currency
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

@OptIn(ExperimentalCoroutinesApi::class)
class OverallViewModel(
    accountsRepository: AccountsRepository,
    balancesRepository: BalancesRepository,
    currenciesRepository: CurrenciesRepository
) : ViewModel(){

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    val baseCurrency: StateFlow<String> = currenciesRepository.getDefaultCurrencyStream()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = "USD"
        )

    val accountsTotalBalance: StateFlow<Pair<Currency, Float>> = accountsRepository
        .totalBalance()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = Pair(Currency("USD", 1.0f, LocalDateTime.now()), 0f)
        )


    private val currentFromDateFlow: MutableStateFlow<YearMonth> = MutableStateFlow(YearMonth.now().minusMonths(5))
    private val currentToDateFlow: MutableStateFlow<YearMonth> = MutableStateFlow(YearMonth.now())

    private val currentDateRangeFlow: Flow<Pair<YearMonth, YearMonth>> = combine(currentFromDateFlow, currentToDateFlow) { fromDate, toDate ->
        Pair(fromDate, toDate)
    }
    val currentDateRange: StateFlow<Pair<YearMonth, YearMonth>> = currentDateRangeFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = Pair(YearMonth.now().minusMonths(5), YearMonth.now())
        )

    fun setCurrentRangeFlow(fromDate: YearMonth, toDate: YearMonth) {
        if (fromDate.isBefore(toDate)) {
            currentFromDateFlow.value = fromDate
            currentToDateFlow.value = toDate
        }
    }

    val lastExpenses: StateFlow<Map<YearMonth,Map<Category,Float>>> = currentDateRangeFlow
        .flatMapLatest { (fromDate, toDate) ->
            balancesRepository.getCurrentBalancesByMonthStream(fromDate, toDate)
        }
        .map{monthMap ->
            //TODO: Improve this code
            val newMap: MutableMap<YearMonth,Map<Category,Float>> = mutableMapOf()
            for ((yearMonth, categoryMap) in monthMap) {
                // Filter categories with negative float values
                val filteredCategoryMap = categoryMap.filter { (_, value) -> value < 0 }
                if (filteredCategoryMap.isNotEmpty()) {
                    newMap[yearMonth] = filteredCategoryMap
                }else{
                    newMap[yearMonth] = mapOf()
                }
            }
            newMap
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = mapOf()
        )

    val lastIncomes: StateFlow<Map<YearMonth,Map<Category,Float>>> = currentDateRangeFlow
        .flatMapLatest { (fromDate, toDate) ->
            balancesRepository.getCurrentBalancesByMonthStream(fromDate, toDate)
        }
        .map { monthMap ->
            val newMap: MutableMap<YearMonth,Map<Category,Float>> = mutableMapOf()
            for ((yearMonth, categoryMap) in monthMap) {
                // Filter categories with positive float values
                val filteredCategoryMap = categoryMap.filter { (_, value) -> value > 0 }
                if (filteredCategoryMap.isNotEmpty()) {
                    newMap[yearMonth] = filteredCategoryMap
                } else {
                    newMap[yearMonth] = mapOf()
                }
            }
            newMap
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = mapOf()
        )


    private val expectedFromDateFlow: MutableStateFlow<YearMonth> = MutableStateFlow(YearMonth.now().minusMonths(5))
    private val expectedToDateFlow: MutableStateFlow<YearMonth> = MutableStateFlow(YearMonth.now())

    private val expectedDateRangeFlow: Flow<Pair<YearMonth, YearMonth>> = combine(expectedFromDateFlow, expectedToDateFlow) { fromDate, toDate ->
        Pair(fromDate, toDate)
    }
    val expectedDateRange: StateFlow<Pair<YearMonth, YearMonth>> = expectedDateRangeFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = Pair(YearMonth.now().minusMonths(5), YearMonth.now())
        )


    val expectedExpenses: StateFlow<Map<YearMonth, Map<Category, Float>>> = expectedDateRangeFlow
        .flatMapLatest { (fromDate, toDate) ->
            balancesRepository.getExpectedBalancesByMonthStream(fromDate, toDate)
        }
        .map { data ->
            val newMap: MutableMap<YearMonth, Map<Category, Float>> = mutableMapOf()
            for ((yearMonth, categoryMap) in data) {
                // Filter categories with negative float values
                val filteredCategoryMap = categoryMap.filter { (_, value) -> value < 0 }
                if (filteredCategoryMap.isNotEmpty()) {
                    newMap[yearMonth] = filteredCategoryMap
                } else {
                    newMap[yearMonth] = mapOf()
                }
            }
            newMap
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = mapOf()
        )


    val expectedIncomes: StateFlow<Map<YearMonth, Map<Category, Float>>> = expectedDateRangeFlow
        .flatMapLatest {(fromDate, toDate) ->
            balancesRepository.getExpectedBalancesByMonthStream(fromDate, toDate)
        }
        .map { data ->
            val newMap: MutableMap<YearMonth, Map<Category, Float>> = mutableMapOf()
            for ((yearMonth, categoryMap) in data) {
                // Filter categories with negative float values
                val filteredCategoryMap = categoryMap.filter { (_, value) -> value > 0 }
                if (filteredCategoryMap.isNotEmpty()) {
                    newMap[yearMonth] = filteredCategoryMap
                } else {
                    newMap[yearMonth] = mapOf()
                }
            }
            newMap
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = mapOf()
        )

    fun setExpectedRangeFlow(fromDate: YearMonth, toDate: YearMonth){
        if(fromDate < toDate){
            expectedFromDateFlow.value = fromDate
            expectedToDateFlow.value = toDate
        }
    }

    private val balanceFromDateFlow: MutableStateFlow<YearMonth> = MutableStateFlow(YearMonth.now().minusMonths(0))
    private val balanceToDateFlow: MutableStateFlow<YearMonth> = MutableStateFlow(YearMonth.now().plusMonths(6))

    private val balanceDateRangeFlow: Flow<Pair<YearMonth, YearMonth>> = combine(
        balanceFromDateFlow, balanceToDateFlow
    ) { fromDate, toDate ->
        Pair(fromDate, toDate)
    }
    val balanceDateRange: StateFlow<Pair<YearMonth,YearMonth>> = balanceDateRangeFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = Pair(YearMonth.now(), YearMonth.now().plusMonths(2))
        )

    fun setBalanceRangeFlow(fromDate: YearMonth, toDate: YearMonth){
        if(fromDate < toDate){
            balanceFromDateFlow.value = fromDate
            balanceToDateFlow.value = toDate
        }
    }

    val balancesByDay: StateFlow<Map<LocalDate, Float>> = balanceDateRangeFlow.flatMapLatest { (fromDate, toDate) ->
        val sundays = generateSequence(fromDate.atDay(1)) { it.plusMonths(1) }
            .takeWhile { it <= toDate.atEndOfMonth() }
            .flatMap { monthStart ->
                generateSequence(monthStart) { it.plusDays(1) }
                    .takeWhile { it.month == monthStart.month }
            }
            .filter { it.dayOfWeek == DayOfWeek.SUNDAY }

        balancesRepository.getBalanceByDay(
            fromDate = fromDate.atDay(1),
            toDate = toDate.atEndOfMonth()
        ).map {
            val newMap: MutableMap<LocalDate, Float> = mutableMapOf()
            for (day in sundays) {
                newMap[day] = it[day] ?: 0f // This will NEVER be null
            }
            newMap
        }
    }
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = mapOf()
    )

}

data class OverallAccountsUiState(
    val accountsList: List<FullAccount> = listOf()
)

