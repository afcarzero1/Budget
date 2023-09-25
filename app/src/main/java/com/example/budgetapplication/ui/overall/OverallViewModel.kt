package com.example.budgetapplication.ui.overall

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgetapplication.data.accounts.AccountsRepository
import com.example.budgetapplication.data.accounts.FullAccount
import com.example.budgetapplication.data.balances.BalancesRepository
import com.example.budgetapplication.data.categories.Category
import com.example.budgetapplication.data.currencies.Currency
import com.example.budgetapplication.data.transactions.TransactionType
import com.example.budgetapplication.data.transactions.TransactionsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDateTime
import java.time.YearMonth

@OptIn(ExperimentalCoroutinesApi::class)
class OverallViewModel(
    accountsRepository: AccountsRepository,
    balancesRepository: BalancesRepository
) : ViewModel(){

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

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


    private val fromDateFlow: MutableStateFlow<YearMonth> = MutableStateFlow(YearMonth.now().minusMonths(5))
    private val toDateFlow: MutableStateFlow<YearMonth> = MutableStateFlow(YearMonth.now())


    val dateRangeFlow: Flow<Pair<YearMonth, YearMonth>> = combine(fromDateFlow, toDateFlow) { fromDate, toDate ->
        Pair(fromDate, toDate)
    }

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


    val expectedExpenses: StateFlow<Map<YearMonth, Map<Category, Float>>> = dateRangeFlow
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


    val expectedIncomes: StateFlow<Map<YearMonth, Map<Category, Float>>> = dateRangeFlow
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

}

data class OverallAccountsUiState(
    val accountsList: List<FullAccount> = listOf()
)

