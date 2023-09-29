package com.example.budgetapplication.ui.overall

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgetapplication.data.accounts.AccountsRepository
import com.example.budgetapplication.data.accounts.FullAccount
import com.example.budgetapplication.data.balances.BalancesRepository
import com.example.budgetapplication.data.categories.Category
import com.example.budgetapplication.data.currencies.CurrenciesRepository
import com.example.budgetapplication.data.currencies.Currency
import com.example.budgetapplication.ui.components.ColorAssigner
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
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

    val accountsColorAssigner: ColorAssigner = ColorAssigner(
        listOf(
            Color(0xFFBB86FC),
            Color(0xFF6200EE),
            Color(0xFF3700B3),
            Color(0xFF03DAC5),
            Color(0xFF007BFF)
        )
    )

    val accountsUiState: StateFlow<OverallAccountsUiState> = accountsRepository
        .getAllFullAccountsStream()
        .map { OverallAccountsUiState(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = OverallAccountsUiState()
        )

    val accountsTotalBalance: StateFlow<Pair<Currency, Float>> = accountsRepository
        .totalBalance()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = Pair(Currency("USD", 1.0f, LocalDateTime.now()), 0f)
        )


    val lastExpenses: StateFlow<Map<YearMonth,Map<Category,Float>>> = balancesRepository
        .getCurrentBalancesByMonthStream(
            fromDate = YearMonth.now().minusMonths(5),
            toDate = YearMonth.now()
        )
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

    val lastIncomes: StateFlow<Map<YearMonth,Map<Category,Float>>> = balancesRepository
        .getCurrentBalancesByMonthStream(
            fromDate = YearMonth.now().minusMonths(5),
            toDate = YearMonth.now()
        )
        .map{monthMap ->
            val newMap: MutableMap<YearMonth,Map<Category,Float>> = mutableMapOf()
            for ((yearMonth, categoryMap) in monthMap) {
                // Filter categories with negative float values
                val filteredCategoryMap = categoryMap.filter { (_, value) -> value > 0 }
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

    //TODO: Use here also the date flow
    val balancesByDay = balancesRepository.getBalanceByDay(
        fromDate = LocalDate.now().minusMonths(5),
        toDate = LocalDate.now().plusMonths(5)
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = mapOf()
    )

}

data class OverallAccountsUiState(
    val accountsList: List<FullAccount> = listOf()
)

